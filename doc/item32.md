# 項目32 ジェネリックと可変長引数を注意して組み合わせる
可変長引数を持つメソッドとジェネリクスは、相性がよくないので、一緒に使うときは気をつけよう。


## 可変長引数とは

メソッドの定義側でひとつ引数を設定しておくと、実際につかうときにいくつも引数を書ける。
```...```となっている引数。

```java
void method(String... args) {
    System.out.println(args.length);
    for (String s : args) {
        System.out.println(s);
    }
}
```

可変長引数を持つメソッドとジェネリクスは、Java5で同時にリリースされたが、相性はよくない。
可変長引数は**漏出抽象化**である。

## 漏出抽象化
可変長引数を持つメソッドを呼び出すときには、可変長引数を保持するために配列が生成され、見えてしまう。この配列は、本来は実装の詳細であるべき。
(抽象化しているのに配列であるかのように感じさせる後述の警告が出るということ？)

その結果、ジェネリック型```List<E>```やパラメータ化された```List<String>```(**これらを具象化不可能型**という。item28より)を可変長引数に渡した場合にはコンパイラから警告が出る。ここで出る警告はヒープ汚染。

## ヒープ汚染
ヒープ汚染は、パラメータ化された型が、その型とは違うオブジェクトを参照しているときに発生する。
コンパイラによる自動的なキャストが失敗し、ジェネリック型が保証する型安全のシステムが破られてしまう可能性がある。

```java
// ジェネリックスと可変長引数の混在は型システムを破る
static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList;             // String型Listに対して、IntegerのListを入れ込む。これがヒープ汚染
    String s = stringLists[0].get(0); // ClassCastException
}
```

このメソッドは目に見えるキャストをしていないにも関わらず、ClassCastExceptionが発生する。実行時に最終行でコンパイラによる自動的なキャストが実行され、失敗する。
よって、**ジェネリック型の可変長引数に値を格納するのは安全でない**。

なぜ、ジェネリック型の可変長引数を持つメソッドが宣言された時点で警告のみで、コンパイルエラーが出ないのか？

それは、ジェネリック型やパラメータ化された型の可変長引数が実装時に役立つので、Javaの言語を作った人がこの不整合を受け入れることにしたから。

実際にJava標準ライブラリでは、このようなメソッドを公開している。
* ```Arrays.asList(T... a),```
* ```Collections.addAll(Collection<? super T> c  T... elements)```
* ```EnumSet.of(E first, E... rest)```
これらは、型安全になっている。

Java7以前は、こうしたジェネリック型の可変長引数を持ったメソッドの呼び出し箇所で現れる警告をクライアント側で対処する方法はなかった。（標準ライブラリであっても？？）
```@SupressWarnings(unchecked)```で潰すか、無視するかの２択。

Java7以降は、SafeVarargsアノテーションなるものが追加された。これをジェネリック型の可変長引数を持ったメソッドに付与することで、呼び出し側で警告が出ることはなくなる。**SafeVarargsアノテーションは、メソッドの作者が、そのメソッドは型安全であると約束していることを表している。**
その代わり、安全でない可能性があっても警告は出ないので、安全なときにのみこのアノテーションをつける。

## 型安全を保証には何が必要か
ジェネリック型の配列は、可変長配列を保持するために、メソッドが呼び出されたときに生成される。(最初のほうの記載参照)

メソッドの処理において、当該の配列への格納を行わず、当該の配列への信用できないコードからの参照を許していない場合には、安全である。
単純に、引数の受け渡しにだけ使われている場合だけ安全。

一歩で、可変長引数の配列に対して、何も保存しなかったとしても、型安全を壊すことは可能。

```java

// 安全ではない - ジェネリックスパラメータ配列への参照を公開している
static <T> T[] toArray(T... args) {
    return args;
}
```

この配列の型は、メソッドの引数に渡された型のコンパイル時の型で決まるが、コンパイラは正確な判断の下すための十分な情報を与えられない。このメソッドは可変長引数の配列を返すので、ヒープ汚染を呼び出し元に伝搬させてしまう。

もう少し具体的に考えてみる。上記のtoArrayを呼び出すメソッドがあるとする。

```java
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
      case 0: return toArray(a, b);
      case 1: return toArray(a, c);
      case 2: return toArray(b, c);
    }
    throw new AssertionError(); // 到達不能
}
```

このメソッドをコンパイルするにあたり、コンパイラは2つのTインスタンスをtoArrayメソッドに渡すための可変長引数配列を生成するコードを生成する。そのコードは、呼び出し元でこのpickTwoメソッドへ渡したオブジェクトの方に関係なく、２つのインスタンスを保持するための配列```Object[]```を割り当てる。
toArrayメソッドは単にこの配列をpickTwoメソッドに返し、pickTwoメソッドが呼び出し元にこの配列を返す。よって、pickTwoメソッドは常に```Object[]```型の配列を返す。

以下のから、pickTwoメソッドを呼び出すとする。

```java
public static void main(String[] args) {
    String[] attributes = pickTwo("Good", "Fast", "Cheap");
}
```

このコードは、コンパイルエラーも警告も出ないが、実行すると明示的にキャストしていないのに、ClassCastExceptionが発生する。これは、pickTwoメソッドの戻り値の型が```Object[]```であるので、それを```String[]```にしようとしているところで発生している。```Object[]```は```String[]```のサブタイプではないために起きる。

この例は、**他のメソッドにジェネリック型の可変長引数配列にアクセスをさせることは安全でない**、ということを示している。
ただし、これには２つの例外がある。
* 正しく@SafeVarargsが付与されているメソッドにその配列を渡すのは安全
* その配列を可変長でない引数のメソッドに、単に配列の内容の演算をかける場合は安全

次は、安全なジェネリック型の可変長引数の使い方の典型例。
入力された任意の個数のリストを受け取り、入力順にすべての要素を含む単一のリストを返す。

ワイルドカード型を使っている。
```java
// ジェネリック可変長パラメータを持つ安全なメソッド
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) { /
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists)
        result.addAll(list);
    return result;
}
```

SafeVarargs アノテーションをつけるか否かの判断は単純で、ジェネリック型やパラメータ化された型の可変長引数を持つ全メソッドにSafeVarargsアノテーションをつける。

まとめると、次の場合にジェネリック可変長引数メソッドは安全である。

1. 可変長引数に何も保存していない
2. 可変長引数を信頼できないコードから参照できるように

また、SafeVarargs アノテーションはオーバーライドできないメソッドにのみ許されている。
これは、すべてのオーバーライドしているメソッドが安全であると保証するのは不可能であるため。

Java8では ```static, final ```のみ。Java9では、 privateのインスタンスメソッドでも許可されている。

SafeVarargs アノテーションを使う以外には、可変長引数をListに替えることが考えられる。そうすると、flattenメソッドは以下のようになる。

```java
static <T> List<T> flatten(List<List<? extends T>> lists) {
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists)
        result.addAll(list);
    return result;
}
```

こうすれば、可変長引数を許すためのstaticファクトリメソッドであるList.ofと組み合わせて使える。

```java
audience = flaten(List.of(a,b,c));
```

この方法のメリットは、型安全であることを保証し、SafeVarargs アノテーションを自身で付与しなくていいところだ。悪いところは、クライアント側のコードが少し冗長になり、少し遅くなるかもしれないところ。

## まとめ
* 可変長引数とジェネリックは相互にうまく動作しない。
* ジェネリック型の可変長引数に値を格納するのは安全でない
* ジェネリック型可変長引数を持つメソッドを書くなら、型安全にすること
* 警告がでるので、@SafeVarargsをつけること

## 参考にしたページ

[【Java】オートボクシング、アンボクシング](https://qiita.com/chihiro/items/870eca6e911fa5cd8e58)

[java.lang.Stringクラスに学ぶ不変オブジェクトという考え方](https://qiita.com/chooyan_eng/items/f0cb376584ee28452dee)

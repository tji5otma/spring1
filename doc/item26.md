# 項目26 原型を使わない

型安全なので、ジェネリックスを使おう


## ジェネリックスとは

型パラメータ（type parameter）を宣言に持つクラスやインタフェースは、ジェネリッククラスやジェネリックインタフェースと呼ばれる。 この２つをまとめてジェネリック型（generic type）として知られている。
例えば、Listインターフェースはリストの要素の型を表す型パラメータEを持つ。
```java
public interface List<E> extends Collection<E>{...}
```
このEを仮型パラメータという。
ジェネリック型は、仮型パラメータに対応する実型パラメータのリストからなるパラメータ化された型(parameterized type)の集合を定義する。
例えば、```List<String>```は、その要素が実型パラメータStringのリストを表すパラメータ化された型。
一方で、ジェネリック型に実型パラメータを与えないことも可能。 これを原型（raw type）と呼ぶ。

用語をまとめると
|名前|例  |
|---|---|---|
|ジェネリック型 |```List<E>```  |
|仮型パラメータ  |```E```  |
|パラメータされた型 |```List<String>```  |
|実型パラメータ  |```String``` |
|原型  |```List``` |

下記から引用
https://www.thekingsmuseum.info/entry/2016/01/28/004927

## 原型

原型を使うリストは以下のように宣言する。
```java
// 私の切手コレクション Stampインスタンスだけを含む
private final Collection stamps = ...;
```

これを利用する側では以下のようなコードを書くことができ、コンパイルして実行することができる。ただし、実行すると ClassCastException が発生してしまう。

```java
// 切手コレクションにコインを追加
stamps.add(new Coin(...));

for(Iterator i = stamps.iterator(); i.hasNext();){
    // コインを取り出す
    Stamp stamp = (Stamp) i.next(); // => ClassCastException の発生
}
```

実行時までバグを見つけられず、かつ、バグを含んでいるコードから離れたところで例外発生する可能性もあり、デバッグが厄介になってしまう。

## ジェネリックス型を使う

```java
private final Collection<Stamp> stamps = ...;
```

この宣言からstampはStampインスタンスだけを
含むことを保証する。
上のように、Coinインスタンスを追加しようとすると、コンパイルエラーになる。
また、コンパイラは、目に見えないキャストを挿入し、キャストの成功を保証してくれる。

```java
    Stamp stamp = i.next();
```

## なぜ原型の使用が許されているのか

>なぜ原型が許されているのでしょうか。
>それは、もともと Java にはジェネリックスが存在しなかったためです。 ジェネリックスは Java 1.5 で初めてサポートされ、移行互換性のために最新の Java でも原型はサポートされています。
>逆にいうと、Java 1.4 以前ではすべてが原型のような仕組みで動作していたのです。

https://www.thekingsmuseum.info/entry/2016/01/28/004927


## 原型はNGだが、```List<Object>``` はOK

新たなコードでListのような原型は利用するべきではないが、```List<Object>``` などの任意のオブジェクトを挿入できる パラメータ化された型を利用することは問題ない。 前者では型検査が行われない一方、後者では型検査が行われ実行時のエラーを防いでくれる。

前者の例。
```java
// 原型ListにObjectを追加するメソッド
private static void unsafeAdd(List list, Object o) {
    list.add(o);
}

// 実行時に失敗 
public static main (String[] args) {
    List<String> strings = new ArrayList<>();
    unsafeAdd(strings, new Integer(42)); // StringのListにIntegerを追加
    Strings = strings.get(0); //コンパイラが自動的にキャストするので、実行時に例外が発生してしまう
}
```

```List<String>```はList型のサブタイプだが、パラメータ化された
```List<Object>```のサブタイプではない。つまり、原型のListを使うと、型の安全性を失うが、```List<Object>```はパラメータ化されているので、型の安全性を失わない。

後者の例。ジェネリックスを使うのでコンパイル時にエラーになる。
```java
public static void unsafeAdd(List<Object> list, Object o) {
   list.add(o);
}
```

## 型を気にしないコレクションには非境界型ワイルドカードを使う

型要素が何であるかを気にしないコレクションに対して原型を使いたくなるかも。 たとえば、２つのセットを比較し、共通の要素の数を返すメソッド。

```java
// 不明な型に対して、原型を使う
static int numElementsInCommon(Set a, Set b) {
    int result = 0;
    for (Object o1 : a) {
        if (b.contains(o1)) {
            result++
        }
    }
    return reuslt;
}
```

コンパイルは可能だが、原型を使っているので、さきほど同様に実行時に例外が出てしまうかもしれない。
このような場合に使うのが、非境界型ワイルドカード。

ジェネリックス型 (Set) の非境界ワイルドカード型は ```Set<?>``` と書く。 何らかのSetという意味。ジェネリックスを利用したいが、実際の型パラメータが分からない、または気にしたくない、という場合に利用する。

```java
static int numElementsInCommon(Set<?> a, Set<?> b) {
    int result = 0;
    for (Object o1 : a) {
        if (b.contains(o1)) {
            result++
        }
    }
    return reuslt;
}
```
原型のコレクションには、どのような要素も入れられるため、切手コレクションにコインを追加して実行時エラーになったときのように、不変式を壊してしまう。
```Collection<?>```にはnull以外の要素は追加できないので、型安全。

## 原型を使うケース

* クラスリテラルに型パラメータを使ったジェネリック型を指定することはできない。List.classやString[].classは許されるが、List< String >やList< ? > は許されない。
クラスリテラル？使ったことがないのでイメージわかず...
https://detail.chiebukuro.yahoo.co.jp/qa/question_detail/q14197622559


* instanceof を型パラメータを使ったジェネリック型に対して使うことはできない。
好ましい方法は、p.123参照。

## まとめ
* 原型は使わず、ジェネリックスを使う
* 実行時ではなくコンパイル時にエラーをチェックできる
* 型パラメータを気にしない場合には、非境界ワイルドカード型を使う
* 原型を使わざるを得ないとき(クラスリテラルと instanceof 演算子)もあるので注意

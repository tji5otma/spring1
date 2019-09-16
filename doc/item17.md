# 項目17 可変性を最小限にする

不変クラスは、設計、実装、使用が可変クラスよりも容易で、誤りにくく安全なのでオススメ。


## 不変クラスとは

インスタンスの生存期間中は、そのインスタンスを変更できないという性質をもつクラス。

## 不変クラスの種類

Javaには String、ボクシングされた基本データクラス(e.g. Integer)、BigInteger、BigDecimal などの不変クラスがある。

例えば、Stringは内部の状態を変更できる術を一切公開していない。Setterがなかったり。

## 不変クラスの５つの規則

1. オブジェクトの状態を変更するためのいかなるメソッドも提供しない
   * Setterなし
2. クラスが拡張できないことを保証する
   * finalクラスにして継承禁止にする
3. すべてのフィールドをfinalにする
4. すべてのフィールドをprivateにする
5. 可変コンポーネントに対する独占的アクセスを保証する
   * クラスが可変オブジェクト(配列等)を参照している場合、クライアントがそのオブジェクトへ参照できないようにする
   * コンストラクタ、アクセッサー、readObject メソッド(項目88)内では、防御的コピー(項目50)を利用する
   
   防御的になっていない悪い例
   ```java
   Date start = new Date();
   Date end = new Date();
   Period p = new Period(start, end); // 自作クラス。finalなフィールドしか持たない
   end.setYear(78); // finalなendを変えられてしまう
   ```
   こういうことを防ぐコンストラクタが防御的コピー。実装例は、P.232参照。

## 不変な複素数クラスを用いた例

複素数の四則演算と実数部・虚数部のアクセッサー等を提供
```java
public class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double realPart() { return re; }
    public double imaginaryPart() { return im; }

    public Complex add(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    public Complex subtract(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    public Complex multiply(Complex c) {
        return new Complex(re * c.re - im * c.im, re * c.im + im * c.re);
    }

    public Complex divide(Complex c) {
        double tmp = c.re * c.re + c.im * c.im;
        return new Complex(
            (re * c.re - im * c.im) / tmp,
            (re * c.im + im * c.re) / tmp);
    }

    ...
}
```

四則演算のメソッドで、オペランド```(re, im)```を変更せずに新たなインスタンスを生成して返している。これを関数的方法と呼ぶ。
一方、オペランドを変更するような方法は手続き的、命令的方法と呼ぶ。
memo: BigDecimalの四則演算も関数的方法？

## メリット・デメリット

### メリット

1. 不変オブジェクトは単純
不変オブジェクトは、正確に、オブジェクトが作られたときの状態を保つことができる。可変オブジェクトは、状態遷移が複雑。信頼して使うことも難しい。誰かが簡単に書き換えられるので。

2. 不変オブジェクトはスレッドセーフ
マルチスレッドでアクセスされても、状態が不正になることはない。そのため、不変オブジェクトは制限なく共有できる。

最もかんたんな共有方法は、頻繁に使われる値を```public static final```の定数にして提供すること。上記のComplexクラスで例えれば

```java
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE = new Complex(1, 0);
public static final Complex I = new Complex(0, 1);
```

3. メモリ量やガベージコレクションのコストが減少
不変クラスは、頻繁に要求されるインスタンスをキャッシュするstaticファクトリメソッドを提供できる。これにより、メモリ量やガベージコレクションのコストが減少する。
ボクシングされた基本データクラス(e.g. Integer)はStaticファクトリメソッドを持つ。

Integerクラスの実装をみると、やはりキャッシュしているらしい。

```java
    public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }
```

4. 防御的コピーを実装する必要なし

5. 不変オブジェクトは追加コストなしでエラーアトミック性を提供する
項目76参照。
>失敗したメソッドの呼び出しは、オブジェクトをそのメソッドの呼び出し前の状態にしておくべき

### デメリット

1. 個々の異なる値に対して別々のオブジェクトを必要とすること

100万ビットからなるBigIntegerの変数の最下位ビットだけを反転させたいとする。

```java
BigInteger moby = ...
moby = moby.flipBit(0); // 反転
```
flipBitメソッドは、もとのインスタンスと1ビットしか変わらないのに、別の100万ビットからなるBigIntegerインスタンスを生成してしまう。この操作には、BigIntegerの大きさに比例した時間と空間がかかる。
memo: ちなみに、java.util.BitSetは、インスタンス自体を変更するので 1ビット変更するだけで済む。
このようなパフォーマンス問題への解決策として、コンパニオンクラスというものがあるらしい。
memo: BigIntegerではやってくれているらしい。時間があれば、実装を見たがったが...

##　設計の選択肢
不変性を保証するために、クラスはサブクラス化を許してはいけない。(序盤にあげた規則2)
finalクラスでも実現可能だが、コンストラクタを private、またはパッケージプライベートにしてstaticファクトリメソッドを提供する方法がある。
memo: パッケージプライベートは、アクセス修飾子を指定しないやつ。

```java
public class Complex {
    private final double re;
    private final double im;

    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public static Complex valueOf(double re, double im) { 
        return new Complex(re, im);
    }
    ...
}
```

この方法は、複数のパッケージプライベートの実装クラスを使えるので、柔軟性がある。
memo: パッケージ内の他のクラスで拡張可能にしたいなら、コンストラクタをパッケージプライベートにするという解釈。finalクラスならこれができない。


### 注意点

>BigInteger と BigDecimal が書かれた当時は、クラスを final にする重要性が理解されておらず、これらのクラスの持つメソッドはすべてオーバーライド可能になっている。 そのため、セキュリティへの配慮が必要なメソッドで BigInteger や BigDecimal を期待する場合には、getClass() を用いて本当に BigInteger や BigDecimal かどうかを確認する必要がある。 確認しない場合、BigInteger のサブクラスが渡され、内部的な状態が露呈する可能性があるからである

## まとめ

1. 可変にすべき正当な理由がないなら、クラスは不変にすべき

2. 妥当な理由がない限り、すべてのフィールドをprivate finalと宣言すべき


>すべてのgetメソッドに対してsetメソッドを書きたい衝動に抵抗してください。

## 参考にしたページ

[【Java】オートボクシング、アンボクシング](https://qiita.com/chihiro/items/870eca6e911fa5cd8e58)

[java.lang.Stringクラスに学ぶ不変オブジェクトという考え方](https://qiita.com/chooyan_eng/items/f0cb376584ee28452dee)

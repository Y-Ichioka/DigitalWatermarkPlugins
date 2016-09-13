#チュートリアル(日本語)
If you want to browse page written in English, please refer to the [English tutorial page](/tutorial_english.md).


##1. ソフトの導入
プラグインを動かすためにソフトの導入を行います。
以下のURLから使用しているOSに対応したものをダウンロードしてください。

http://imagej.net/Fiji/Downloads

ImageJ上でもプラグインは動作しますが、より多くの標準機能を搭載したFijiを推奨します。
またFijiの使用を前提として以下の説明をします。


##2. プラグインの導入
```git clone https://github.com/test/test```するかブラウザ上からリポジトリをダウンロードしてください。
その後pluginsディレクトリの中にDigitalWatermarkディレクトリを入れて下さい。
以下のようなディレクトリ構造になります。

```
plugins
├─Analyze
├─Examples
├─JRuby
├─Macros
├─Scripts
├─Utilities
└─DigitalWatermark <= new
```

ソフトを再起動後、メニュー上のボタンの*Plugins*->*DigitalWatermark*から任意の処理を行えます。


##3. ビット置換法を行ってみる
例として実際にプラグインを使用して画像電子透かしを行います。
ビット置換法を用いてデータを画像に対し埋め込みを行います。

###埋め込み処理

####1. 画像をビットプレーンに変換
画像をビットプレーンに分割します。
https://ja.wikipedia.org/wiki/%E3%83%93%E3%83%83%E3%83%88%E3%83%97%E3%83%AC%E3%83%BC%E3%83%B3

![my image](image/my_image.png)


####2. 埋め込むデータをビットプレーンに変換
####3. ビットプレーンを画像に変換

###抽出処理

####1. 画像をビットプレーンに変換
####2.　任意のビットプレーンからデータを抽出


##4. その他プラグイン
3節で用いなかったその他のプラグインはドキュメントを参照してください。


##5. 動画の読み込み、書き出し
###読み込み
メニュー上のボタンの*File*->*Open...*から行えます。

読み込みが可能な動画はJPEG、PNG圧縮か無圧縮のAVI形式に限られます。
[FFmpeg](https://www.ffmpeg.org/)を用いて以下のコマンドを命令することにより、無圧縮のAVI形式に変換することができます。

```
ffmpeg -i input.mp4 -an -vcodec rawvideo -y output.avi
```

###書き出し
メニュー上のボタンの*Plugins*->*Bio-Formats*->*Bio-Formats Exporter*から行えます。


##6. マクロとプラグイン
ImageJ,Fijiではマクロの作成が容易に行えるため、複数画像に対する評価などはマクロを用いると便利です。

プラグインにはOpenCVを用いることが可能であったり、FijiではPythonを使用できます。


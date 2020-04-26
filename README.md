# anthum-sparkline

This app generates a PNG sprite with sparkline graphs.  In certain situations, a server-side sprite will outperform browser-generated sparklines that use SVG or canvas.

Examples using random data are below, and you would implement your own sparklines by interfacing with `SparklineGenerator.createPNG()` and passing your data and options to it. 

#### Total Sparklines\*
https://demo.anthum.com/sparkline.png?bg=cccccc&total=10<br />
https://demo.anthum.com/sparkline.png?bg=cccccc&total=11<br />
https://demo.anthum.com/sparkline.png?bg=cccccc&total=100

<sup>\* Background color specified because it's transparent otherwise (barely visible in latest Chrome)</sup>

#### Line Color
https://demo.anthum.com/sparkline.png?color=aa0000

#### Background Color
https://demo.anthum.com/sparkline.png?color=333333&bg=eaeaea

#### Fill Color
Fill is experimental and is not pixel perfect. You'll see overflows over boundaries, but feel free to play with it

https://demo.anthum.com/sparkline.png?color=333333&bg=eaeaea&fill=aaaaaa<br>
https://demo.anthum.com/sparkline.png?color=ff0000&bg=000000&fill=666666

#### Predictible Randomness

Add `id` parameter to use consistent/predictible randomness when testing color options.

https://demo.anthum.com/sparkline.png?id=1&color=333333<br>
https://demo.anthum.com/sparkline.png?id=1&color=aaaaaa

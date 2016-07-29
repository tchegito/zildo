
# Define linear function in Pixel Shader #

In order to clip text display in the Dialog Hystory, we wanted to use pixel shader. Basically, the idea was using the 'y' coordinate to return alpha channel attribute.
Then the higher part could be hidden smoothly, so as the lower part, and we'll get a great render.

All we need is a function, taking 'y' coordinate as input, and returns an alpha channel value in float range [0..1], like in the following graphic:

![Courbe](https://raw.githubusercontent.com/tchegito/zildo/wiki/courbe.png)

We can use the following function:
<pre>
 f(y) = (max-2a) - abs(2y - max)
</pre>

It only lacks clamping, to get a result in range [0..1], which we can do with:
<pre>
 X = max(min(x, 1), 0)
</pre>

So finally, function is the following one:
<pre>
 f(y) = max(min((max-2a) - abs(2y - max), 1), 0)
</pre>

Eventually, we can divide result before clamping to get a pretty render like this:

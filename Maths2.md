# Bezier curves (3 points) #

Sometimes, to get a smoother movement, a Bezier's curve is a great tool. We only have to use De Casteljau resolution algorithm to get it through.

First use was the dragon's neck. We fix 2 points and calculates the third one, which is between the two others.

![https://lh5.googleusercontent.com/-Cshn2X5xhaE/VDr291y5opI/AAAAAAAAB3k/7hPGL57ynIo/w257-h124-no/bezier3.png](https://lh5.googleusercontent.com/-Cshn2X5xhaE/VDr291y5opI/AAAAAAAAB3k/7hPGL57ynIo/w257-h124-no/bezier3.png)

As a reminder, here's the general formula, to calculate interpolation between point b<sub>i</sub> and b<sub>i+1</sub>:

b<sub>i</sub><sup>r</sup> (t) = (1-t) b<sub>i</sub><sup>r-1</sup> (t) + t.b<sub>i+1</sub><sup>r-1</sup> (t)

When we replace each variables and apply for 3 points, we get this:

b<sub>0</sub><sup>2</sup> (t) = (1-t)² b<sub>0</sub> (t) + (t(1-t))².b<sub>1</sub> (t) + t².b<sub>2</sub> (t)

We just replace each b by x,y coordinates and it's ok.

Basically, in the code, this is implemented by [Bezier3 class](https://github.com/tchegito/zildo/blob/master/zildo/src/main/java/zildo/monde/Bezier3.java)

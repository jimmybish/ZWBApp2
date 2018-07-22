# The ZWB App

In 2013, while participating in the roller derby community as a referee, I notices there was no easy way to view the official rules on a mobile device. I decided that was the time to give Android development a crack!

The app was a single Activity app with fragments used to display all sections. It consisted of a number of elements, including a web scraper, which stored all rules in a local SQLite database, an RSS reader, and a number of static pages to display images.
The fragmented layout utilised a 3rd party library which allowed size and rotation based layout switching. In a tablet/landscape layout (if the DPI was high enough), each child fragment would spawn to the right of its parent, with a slight overlap. Swiping left and right would show/hide parent and child fragments, allowing dynamic navigation by swiping in any direction.
On lower DPI devices, it would behave like most mobile apps with ListView fragments.

After some private testing among Tasmanian league members, it was released on the Play Store. It gained 496 installs in a little over a week and got some great feedback!

Unfortunately, less than 2 weeks after going live in the Play Store, WFTDA's lawyer sent a cease and decist and I took it down.

![The first Share on Facebook](/screenshots/ZWB%20Share.png?raw=true "The first Share on Facebook")

![Downloads by region](/screenshots/ZWB%20Stats%202.png?raw=true "Downloads by region")

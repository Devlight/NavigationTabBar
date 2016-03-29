NavigationTabBar
===================

Navigation tab bar with colorful interactions.

Horizontal NTB|Vertical NTB|NTB Samples|
:-------------------------:|:-------------------------:|:-------------------------:
![](https://lh6.googleusercontent.com/-Bf7uxUiRvfk/VvpVlkZzsVI/AAAAAAAACPA/Ysg9uuBpaL8UhsXpYPlyNJK6IJssdkMvg/w325-h552-no/hntb.gif)|![](https://lh4.googleusercontent.com/-k4Ac7-c2m8E/VvpVlk3ZmLI/AAAAAAAACPA/21ISoAYGZzUlvGPmIauXwfYZOKdCYIRGg/w323-h552-no/vntb.gif)|![](https://lh5.googleusercontent.com/-hmELfZQvexU/VvpVlooaPvI/AAAAAAAACPA/5HA5ic7dASwBUYqpqcfxAmfLzPPDXejqQ/w322-h552-no/ntbs.gif)

U can check the sample app [here](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/tree/master/app).

Download
------------

You can download a .aar` from GitHub's [releases page](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/releases).

Or use Gradle jCenter:

```groovy
dependencies {
    repositories {
        mavenCentral()
        maven {
            url  'http://dl.bintray.com/gigamole/maven/'
        }
    }
    compile 'com.github.devlight.navigationtabbar:library:+'
}
```

Or Gradle Maven Central:

```groovy
compile 'com.github.devlight.navigationtabbar:library:1.0.0'
```

Or Maven:

```groovy
<dependency>
    <groupId>com.github.devlight.navigationtabbar</groupId>
    <artifactId>library</artifactId>
    <version>1.0.0</version>
    <type>aar</type>
</dependency>
```

Android SDK Version
=========

NavigationTabBar requires a minimum sdk version of 11. 

Sample
========

For NTB you can set such parameters as:
 
 - models:
    
    allows you to set NTB models, where you set icon color. Can be set up only via code.
    
 - view pager:
     
     allows you to connect NTB with ViewPager. If you want your can also set OnPageChangeListener.    

 - corners radius:
    
    allows you to set corners radius of pointer.

 - animation duration:
  
     allows you to set animation duration.
      
 - inactive color:

     allows you to set inactive icon color.
     
 - active color:
  
     allows you to set active icon color.
     
 - tab bar listener:
  
     allows you to set listener which triggering on start or on end when you set model index.
        
 - preview colors:
  
     allows you to set preview colors, which generate count of models equals to count of colors.

Orientation automatically detected according to view size.

If your set ViewPager you can action down on active pointer and do like drag.

Check out in code init:

```java
final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);
final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
models.add(new NavigationTabBar.Model(
        getResources().getDrawable(R.drawable.ic_first), colors[0]));
models.add(new NavigationTabBar.Model(
        getResources().getDrawable(R.drawable.ic_second), colors[1]));
models.add(new NavigationTabBar.Model(
        getResources().getDrawable(R.drawable.ic_third), colors[2]));
models.add(new NavigationTabBar.Model(
        getResources().getDrawable(R.drawable.ic_fourth), colors[3]));
models.add(new NavigationTabBar.Model(
        getResources().getDrawable(R.drawable.ic_fifth), colors[4]));
navigationTabBar.setModels(models);
navigationTabBar.setViewPager(viewPager, 2);
```
            
Other methods check out in sample.

And XML init:

```xml
<com.gigamole.library.NavigationTabBar
   android:id="@+id/ntb"
   android:layout_width="match_parent"
   android:layout_height="50dp"
   app:ntb_animation_duration="400"
   app:ntb_preview_colors="@array/colors"
   app:ntb_corners_radius="10dp"
   app:ntb_active_color="#fff"
   app:ntb_inactive_color="#000"/>
```

Getting Help
======

To report a specific problem or feature request, [open a new issue on Github](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/issues/new).

License
======

Apache 2.0 and MIT. See [LICENSE](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/blob/master/LICENSE.txt) file for details.

Inspiration
======

Tapbar interections|Circle interactions
:-------------------------:|:-------------------------:
![](https://s-media-cache-ak0.pinimg.com/originals/39/ee/33/39ee330f3460bd638284f0576bc95b65.gif)|![](https://s-media-cache-ak0.pinimg.com/564x/f4/0d/a9/f40da9e5b73eb5e0e46681eba38f1347.jpg)

TODO
======

 - model with title
        
 - model with badge

Author
=======

Made in [DevLight Mobile Agency](https://github.com/DevLight-Mobile-Agency)

Created by [Basil Miller](https://github.com/GIGAMOLE) - [@gigamole](mailto:http://gigamole53@gmail.com)
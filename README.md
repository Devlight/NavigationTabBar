[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-NavigationTabBar-blue.svg?style=flat-square)](http://android-arsenal.com/details/1/3382)

NavigationTabBar
===================

Navigation tab bar with colorful interactions.

Horizontal NTB|NTB bottom, badge, all title |NTB top, badge, typeface, active title|Vertical NTB|NTB Samples|
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
![](https://lh6.googleusercontent.com/-Bf7uxUiRvfk/VvpVlkZzsVI/AAAAAAAACPA/Ysg9uuBpaL8UhsXpYPlyNJK6IJssdkMvg/w325-h552-no/hntb.gif)|![](https://lh4.googleusercontent.com/-hxXHKG4zMOU/VwLWxDdhxQI/AAAAAAAACQg/gErfodzZlpINFmlWllvuFs6dlRnp_rG9w/w322-h551-no/tbntb.gif)|![](https://lh5.googleusercontent.com/-3RKqh-MquqA/VwLWxHKv2jI/AAAAAAAACQg/WjmW9OravjIAzinLVFXEditNN4DFfRt6A/w322-h552-no/ttbntb.gif)|![](https://lh4.googleusercontent.com/-k4Ac7-c2m8E/VvpVlk3ZmLI/AAAAAAAACPA/21ISoAYGZzUlvGPmIauXwfYZOKdCYIRGg/w323-h552-no/vntb.gif)|![](https://lh5.googleusercontent.com/-hmELfZQvexU/VvpVlooaPvI/AAAAAAAACPA/5HA5ic7dASwBUYqpqcfxAmfLzPPDXejqQ/w322-h552-no/ntbs.gif)

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
compile 'com.github.devlight.navigationtabbar:library:1.1.3'
```

Or Maven:

```groovy
<dependency>
    <groupId>com.github.devlight.navigationtabbar</groupId>
    <artifactId>library</artifactId>
    <version>1.1.3</version>
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

 - model title:
    
    allows you to enable title in you model.
    
 - model badge:
     
    allows you to enable badge in you model.
     
 - use custom typeface on badge:
     
    allows you to handle set of custom typeface in your badge.
    
 - title mode:
   
     allows you to handle mode of the model title show. Can show all or only active.
     
 - badge position:
 
    allows you to set the badge position in you model. Can be: left(25%), center(50%) and right(75%).

 - badge gravity:
  
    allows you to set the badge gravity in NTB. Can be top or bottom.
    
 - typeface:
 
    allows you to set custom typeface to your title.
 
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

navigationTabBar.setTitleMode(NavigationTabBar.TitleMode.ACTIVE);
navigationTabBar.setBadgeGravity(NavigationTabBar.BadgeGravity.BOTTOM);
navigationTabBar.setBadgePosition(NavigationTabBar.BadgePosition.CENTER);
navigationTabBar.setTypeface("fonts/custom_font.ttf");
navigationTabBar.setIsBadged(true);
navigationTabBar.setIsTitled(true);
navigationTabBar.setIsBadgeUseTypeface(true);
```

If you want to set the background to NTB, you need to create some view at he bottom of NTB in layout and then set height of your background view like this:

```java
navigationTabBar.post(new Runnable() {
    @Override
    public void run() {
        final View background = findViewById(R.id.background);
        background.getLayoutParams().height = (int) navigationTabBar.getBarHeight();
        background.requestLayout();
    }
});
```

If your models is in badge mode you can set title, hide, show, toggle and update badge title like this:

```java
model.setTitle("Here some title to model");
model.hideBadge();
model.showBadge();
model.toggleBadge();
model.updateBadgeTitle("Here some title like NEW or some integer value");
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
   app:ntb_inactive_color="#000"
   app:ntb_badged="true"
   app:ntb_titled="true"
   app:ntb_title_mode="all"
   app:ntb_badge_position="right"
   app:ntb_badge_gravity="top"
   app:ntb_typeface="fonts/custom_typeface.ttf"
   app:ntb_badge_use_typeface="true"/>
```

Getting Help
======

To report a specific problem or feature request, [open a new issue on Github](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/issues/new).

License
======

Apache 2.0 and MIT. See [LICENSE](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/blob/master/LICENSE.txt) file for details.

Inspiration
======

Tapbar interactions| Circle interactions | Title interactions
:-------------------------:|:-------------------------:|:-------------------------:
![](https://s-media-cache-ak0.pinimg.com/originals/39/ee/33/39ee330f3460bd638284f0576bc95b65.gif)|![](https://s-media-cache-ak0.pinimg.com/564x/f4/0d/a9/f40da9e5b73eb5e0e46681eba38f1347.jpg)|![](https://s-media-cache-ak0.pinimg.com/564x/14/eb/dd/14ebddfc0d92f02be3d61ede48a9da6e.jpg)

Thanks to [Valery Nuzhniy](https://www.pinterest.com/hevbolt/) for NTB badge design.

Author
=======

Made in [DevLight Mobile Agency](https://github.com/DevLight-Mobile-Agency)

Created by [Basil Miller](https://github.com/GIGAMOLE) - [@gigamole](mailto:http://gigamole53@gmail.com)
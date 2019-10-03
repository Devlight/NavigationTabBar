<br/>
<p align="center">
  <a href="http://devlight.io">
      <img src ="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScLVBKck51Z1Rzb0E" alt="Devlight"/>
  </a>
</p>
<br/>

NavigationTabBar
================

Navigation tab bar with colorful interactions.

[![Android Arsenal](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScbFFTYko2dlc2d28)](http://android-arsenal.com/details/1/3382)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[![Android](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wSccEZaclNGN0R5OWc)](https://github.com/DevLight-Mobile-Agency)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[![Download](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScZE8wb0xKbC1RT0U)](https://bintray.com/gigamole/maven/navigationtabbar/_latestVersion)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[![License](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScU0tmeFpGMHVWNWs)](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/blob/master/LICENSE.txt)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[![Codacy](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScSHhmckZyeGJDcXc)](https://www.codacy.com/app/gigamole53/NavigationTabBar?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DevLight-Mobile-Agency/NavigationTabBar&amp;utm_campaign=Badge_Grade)

<table align="center">
    <tr>
        <td><img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScTEVDQXJLOGZLZFU"/></td>
        <td><img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScTmhyQl9RYVIyUFE"/></td>
        <td><img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScTmhyQl9RYVIyUFE"/></td>
        <td><img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScTDVwbm1qclB1MmM"/></td>
        <td><img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScVWp3eGV4dEJTN2M"/></td>
        <td><img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScdmloRFNKV3hfS0U"/></td>
        <td><img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScclZSSlU2ZE1qVVU"/></td>
    </tr>
</table>

You can check the sample app [here](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/tree/master/app).

Warn
====
```
This library is not more supported. 
If you want to add new feature or fix a bug, grab source code and do it. 
If you think your fix or feature would be useful to other developers, 
I can add link of your repository to this README file. 
Thank you for using our libraries.
```

Download
========

You can download a `.aar` from GitHub's [releases page](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/releases).

You can use Gradle:
```groovy
compile 'devlight.io:navigationtabbar:1.2.5'
```

Or Maven:
```groovy
<dependency>
    <groupId>devlight.io</groupId>
    <artifactId>navigationtabbar</artifactId>
    <version>1.2.5</version>
    <type>aar</type>
</dependency>
```

Or Ivy:
```groovy
<dependency org='devlight.io' name='navigationtabbar' rev='1.2.5'>
  <artifact name='$AID' ext='pom'></artifact>
</dependency>
```

Android SDK Version
===================

`NavigationTabBar` requires a minimum SDK version of 11. 

Sample
======

<b>Parameters</b>

For `NTB` you can set such parameters as:
 
 - models:  
     allows you to set `NTB` models, where you set icon and color. Can be set up only via code.
    
 - behavior:  
     allows you to set bottom translation behavior.
    
 - view pager:  
     allows you to connect `NTB` with `ViewPager`. If you want your can also set `OnPageChangeListener`.
         
 - background color:  
    allows you to set background to `NTB` which automatically set with offset relative to badge gravity and corners radius.

 - model selected icon:  
     allows you to set selected icon when current model is active.

 - model title:  
     allows you to enable title in you model.
    
 - model badge:  
     allows you to enable badge in you model.
     
 - use custom typeface on badge:  
     allows you to handle set of custom typeface in your badge.
    
 - title mode:  
     allows you to handle mode of the model title show. Can show all or only active.
     
 - title size:  
     allows you to set titles size. 
     
 - scale mode:  
     allows you to handle mode of the model icon and title scale.
     
 - tint mode:  
     allows you to enable or disable icon tinting.
      
 - badge size:  
     allows you to set badges size.
     
 - badge position:  
     allows you to set the badge position in you model. Can be: left(25%), center(50%) and right(75%).

 - badge gravity:  
     allows you to set the badge gravity in NTB. Can be top or bottom.
     
 - badge colors:  
     allows you to set the badge bg and title colors.
    
 - typeface:  
     allows you to set custom typeface to your title.
 
 - corners radius:  
     allows you to set corners radius of pointer.

 - icon size fraction:  
     allows you to set icon size fraction relative to smaller model side.

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

<b>Tips</b>

Creation of models occurs through `Builder` pattern.  
`ModelBuilder` requires two fields: icon and color. Title, badge title and selected icon is optional.

You can set selected icon. Resize and scale of selected icon equals to original icon.  
Orientation automatically detected according to `View` size.

By default badge bg color is the active model color and badge title color is the model bg color. To reset colors just set `AUTO_COLOR` value to badge bg and title color.  
By default badge sizes and title sizes is auto fit. To reset calculation just set `AUTO_SIZE` value to badge size and title size.  
By default icon size fraction is `0.5F` (half of smaller side of `NTB` model). To reset scale fraction of icon to automatic just put in method `AUTO_SCALE` value.

If your set `ViewPager` and enable swipe you can action down on active pointer and do like drag.

<b>Init</b>

Check out in code init:

```java
final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);
final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
models.add(
        new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_first),
                Color.parseColor(colors[0])
        ).title("Heart")
                .badgeTitle("NTB")
                .build()
);
models.add(
        new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_second),
                Color.parseColor(colors[1])
        ).title("Cup")
                .badgeTitle("with")
                .build()
);
models.add(
        new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_third),
                Color.parseColor(colors[2])
        ).title("Diploma")
                .badgeTitle("state")
                .build()
);
models.add(
        new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_fourth),
                Color.parseColor(colors[3])
        ).title("Flag")
                .badgeTitle("icon")
                .build()
);
models.add(
        new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_fifth),
                Color.parseColor(colors[4])
        ).title("Medal")
                .badgeTitle("777")
                .build()
);
navigationTabBar.setModels(models);
navigationTabBar.setViewPager(viewPager, 2);

navigationTabBar.setTitleMode(NavigationTabBar.TitleMode.ACTIVE);
navigationTabBar.setBadgeGravity(NavigationTabBar.BadgeGravity.BOTTOM);
navigationTabBar.setBadgePosition(NavigationTabBar.BadgePosition.CENTER);
navigationTabBar.setTypeface("fonts/custom_font.ttf");
navigationTabBar.setIsBadged(true);
navigationTabBar.setIsTitled(true);
navigationTabBar.setIsTinted(true);
navigationTabBar.setIsBadgeUseTypeface(true);
navigationTabBar.setBadgeBgColor(Color.RED);
navigationTabBar.setBadgeTitleColor(Color.WHITE);
navigationTabBar.setIsSwiped(true);
navigationTabBar.setBgColor(Color.BLACK);
navigationTabBar.setBadgeSize(10);
navigationTabBar.setTitleSize(10);
navigationTabBar.setIconSizeFraction(0.5);
```

If your models is in badge mode you can set title, hide, show, toggle and update badge title like this:
```java
model.setTitle("Here some title to model");
model.hideBadge();
model.showBadge();
model.toggleBadge();
model.updateBadgeTitle("Here some title like NEW or some integer value");
```
          
To enable behavior translation inside `CoordinatorLayout` when at bottom of screen:
```java
navigationTabBar.setBehaviorEnabled(true);
```

To deselect active index and reset pointer:
```java
navigationTabBar.deselect();
```

Other methods check out in sample.

And XML init:

```xml
<devlight.io.library.ntb.NavigationTabBar
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
   app:ntb_scaled="true"
   app:ntb_tinted="true"
   app:ntb_title_mode="all"
   app:ntb_badge_position="right"
   app:ntb_badge_gravity="top"
   app:ntb_badge_bg_color="#ffff0000"
   app:ntb_badge_title_color="#ffffffff"
   app:ntb_typeface="fonts/custom_typeface.ttf"
   app:ntb_badge_use_typeface="true"
   app:ntb_swiped="true"
   app:ntb_bg_color="#000"
   app:ntb_icon_size_fraction="0.5"
   app:ntb_badge_size="10sp"
   app:ntb_title_size="10sp"/>
```

XML属性中文详解:
```xml
<devlight.io.library.ntb.NavigationTabBar 各属性详解
    全局:
    app:ntb_bg_color="#000"             ntb的背景颜色                可自定义
    app:ntb_active_color="#000"         ntb活动时的图标+标题颜色      可自定义
    app:ntb_inactive_color="#0f0"       ntb不活动时的图标+标题颜色    可自定义
    app:ntb_corners_radius="10dp"       ntb切换时的动画弧度大小       可自定义
    app:ntb_animation_duration="1000"   ntb切换时的动画时间           单位:ms
    图标相关:
    app:ntb_icon_size_fraction="1"      图标所占的大小比例            最佳值:0.5
    标题相关:
    app:ntb_titled="true"               是否显示图标所对应的标题       默认为false
    app:ntb_title_mode="active"         图片所对应的标题显示模式       active:活动时才显示 all:总是显示  PS:app:ntb_titled属性值为 true 时才可用
    app:ntb_title_size="10sp"           设置图标所对应的标题文字大小    请自定义
    勋章相关:
    app:ntb_badged="false"              是否显示勋章                  默认为false
    app:ntb_badge_gravity="top"         勋章的上下位置                top|bottom
    app:ntb_badge_position="right"      勋章的左右位置                left(25%), center(50%) and right(75%)
    app:ntb_badge_bg_color="#ffff0000"  勋章的背景颜色                可自定义
    app:ntb_badge_title_color="#000000" 勋章的标题文字颜色             可自定义 PS:不设置的话默认为切换动画的背景色
    app:ntb_badge_size="12sp"           勋章的标题文字大小             可自定义
    字体相关:
    app:ntb_badge_use_typeface="false"  是否使用自定义字体             默认为false
    app:ntb_typeface="fonts/by3500.ttf" 设置ntb的自定义字体            请将自定义的字体文件放在 asset/fonts 文件夹下
    其他:
    app:ntb_preview_colors="@array/colors"
    app:ntb_scaled="true"
    app:ntb_tinted="true"
    app:ntb_swiped="true"/>
```

Getting Help
============

To report a specific problem or feature request, [open a new issue on Github](https://github.com/DevLight-Mobile-Agency/NavigationTabBar/issues/new).

Xamarin
=======

Thanks to [Martijn van Dijk](https://github.com/martijn00) for developing Xamarin bindings library for [NavigationTabBar](https://github.com/martijn00/NavigationTabBarXamarin).  
Plugin is available on [Nuget](https://www.nuget.org/packages/Xam.Plugins.Android.NavigationTabBar/).

## use navbar using materialize css
navbar using materialize css is really easy and would take just assigning right classes to the html tags and it would create a navigation tab bar using its prewritten css and js files. This can be easily used in html pages using downloaded files or cdn links...
https://materializecss.com/navbar.html

Credits
=======

Sincere thanks, to portal [FAnDroid.info](http://www.fandroid.info) [(StartAndroid)](https://www.youtube.com/channel/UCzE7HcbvyEiS5ea1rVRbPLQ) who released the review of this library in detail. If you understand the Russian language, then feel free to see the video or read the [text version](http://www.fandroid.info/obzor-biblioteki-navigationtabbar-dlya-android-ot-komandy-devlight/) of its great post.
<p align="center">
    <a href="https://youtu.be/nGikO-tbSsg">
        <img src="https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScSGg0TVBodk9ZM2M"/>
    </a>
</p>

Inspired by:

|[Sergey Valiukh](https://dribbble.com/SergeyValiukh)|
|:--------------------------------------------------:|
|[![](https://s-media-cache-ak0.pinimg.com/originals/39/ee/33/39ee330f3460bd638284f0576bc95b65.gif)](https://dribbble.com/shots/2071319-GIF-of-the-Tapbar-Interactions)|

Thanks to [Valery Nuzhniy](https://www.pinterest.com/hevbolt/) for NTB badge design.

Author
======

Created by [Basil Miller](https://github.com/GIGAMOLE) - [@gigamole](mailto:gigamole53@gmail.com)

Company
=======

[![Facebook](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScaGhGVFNKU0VxMnc)](https://www.facebook.com/devlightagency)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[![Twitter](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wScZ1ExQWh5cHF5cVE)](https://twitter.com/DevLightIO)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[![LinkedIn](https://drive.google.com/uc?export=download&id=0BxPO_UeS7wSccGZINzEycE1nVFE)](https://www.linkedin.com/company/devlight)

[Here](https://github.com/DevLight-Mobile-Agency) you can see open source work developed by Devlight LLC.  
This and another works is an exclusive property of Devlight LLC. 

If you want to use this library in applications which will be available on Google Play, please report us about it or author of the library.

Whether you're searching for a new partner or trusted team for creating your new great product we are always ready to start work with you. 

You can contact us via info@devlight.io or opensource@devlight.io.  
Thanks in advance.

Devlight LLC, 2016  
[devlight.io](http://devlight.io) 

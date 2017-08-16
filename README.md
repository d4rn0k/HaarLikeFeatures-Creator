Haar like features Creator
--------
Graphical tool for creating xml features on images


### What is Haar-like feature?
digital features commonly used in object recognition on images. There are two main types of that features:
 * Rectangle
 * Tilted rectangle


Screenshoot:
![Screenshot](docs/images/screenshot.png?raw=true "Screenshot Windows 10")

**Description:**
1. Initial point
2. Rectangle type Haar like feature
3. Rotated rectangle type Haar like feature
4. Haar-like features list-box
5. Position of initial point
6. Calculated data of selected rectangle

### How to use?

Add new rectangle or tilted rectangle via menu, or right box.
You can move initial point and rectangles. Rectangles also can be resized via dragging on edges.
Finally, you can save as .xml with File -> "Save Haar-like features as .xml" option.

Example output **xml**:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<HaarFeature>

    <initialPointX>11</initialPointX>
    <initialPointY>15</initialPointY>
    
    <rect isRotated="false">
        <x>70</x>
        <y>93</y>
        <width>100</width>
        <height>46</height>
    </rect>
    
    <rect isRotated="true">
        <x>218</x>
        <y>75</y>
        <width>47</width>
        <height>44</height>
    </rect>
    
</HaarFeature>
```
Haar-like features Creator
--------
Graphical tool for creating Haar-like rectangle based features on images



### What is Haar-like feature?
Digital features commonly used in object recognition on images.
 There are two main types of that features:
 * Rectangle
 * Tilted rectangle
  
For example, we can use difference between sum of pixels of two areas, and check relation they describe.

This difference is then used to categorize subsections of an image.
 
For example, let us say we have an image database with human faces. 
It is a common observation that among all faces the region of the eyes is darker than the region of the cheeks.
Therefore a common Haar feature for face detection is a set of two adjacent rectangles that lie above the eye and 
the cheek region. 
The position of these rectangles is defined relative to a detection window that acts like a bounding box to the 
target object - the face in this case.




### Why?
For debugging and checking Haar-like features calculation.


<p align="center">
  <img src="https://github.com/d4rn0k/HaarLikeFeatures-Creator/raw/master/docs/images/screenshot.png?raw=true" 
  alt="Screenshot Windows 10"/>
</p>


**Description:**
1. Initial point
2. Rectangle type Haar-like feature
3. Rotated rectangle type Haar-like feature
4. Haar-like features list-box
5. Position of initial point
6. Calculated data of selected rectangle:

- 4 bounds points
- Area
- Sum of pixels in the area
- Average of pixels in the area

 255 - Max value for 1 Byte pixel = White
 
 0   - Min value for 1 Byte pixel = Black


### How to run?

##### Clone
```
git clone https://github.com/d4rn0k/HaarLikeFeatures-Creator.git
```

##### Compile
```
cd HaarLikeFeatures-Creator/
mvn install 
```

##### Run
```
java -jar target/haar-like-feature-creator-1.0-SNAPSHOT.jar
```

### How to use?
Add new rectangle or tilted rectangle via menu, or right box.
You can move initial point and rectangles. 
Rectangles also can be resized via dragging on edges.
Finally, you can save your work as .xml file with option:   
**File** -> "**Save Haar-like features as .xml**".

### Example output xml:
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

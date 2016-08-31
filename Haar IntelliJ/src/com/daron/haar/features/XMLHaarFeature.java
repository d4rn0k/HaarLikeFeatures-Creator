package com.daron.haar.features;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "HaarFeature")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLHaarFeature {

    int initialPointX;
    int getInitialPointY;
    @XmlElement(name = "rect")
    List<XMLHaar> haarFeature;

    public XMLHaarFeature(int initialPointX, int getInitialPointY, List<XMLHaar> haarFeatures) {
        this.initialPointX = initialPointX;
        this.getInitialPointY = getInitialPointY;
        this.haarFeature = haarFeatures;
    }

//    public XMLHaarFeature() {
//    }
}


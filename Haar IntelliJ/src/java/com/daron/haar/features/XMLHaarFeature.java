package com.daron.haar.features;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "HaarFeature")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLHaarFeature {

    private final int initialPointX;
    private final int getInitialPointY;
    @XmlElement(name = "rect")
    private final List<XMLHaar> haarFeature;

    public XMLHaarFeature() {

        initialPointX = 0;
        getInitialPointY = 0;
        haarFeature = null;
    }

    public XMLHaarFeature(int initialPointX, int getInitialPointY, List<XMLHaar> haarFeatures) {
        this.initialPointX = initialPointX;
        this.getInitialPointY = getInitialPointY;
        this.haarFeature = haarFeatures;
    }

}


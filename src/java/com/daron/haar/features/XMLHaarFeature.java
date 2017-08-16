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
    private final int initialPointY;

    @XmlElement(name = "rect")
    private final List<XMLHaar> haarFeature;

    public XMLHaarFeature() {

        initialPointX = 0;
        initialPointY = 0;
        haarFeature = null;
    }

    public XMLHaarFeature(int initialPointX, int initialPointY, List<XMLHaar> haarFeatures) {
        this.initialPointX = initialPointX;
        this.initialPointY = initialPointY;
        this.haarFeature = haarFeatures;
    }
}


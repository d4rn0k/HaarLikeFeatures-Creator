package com.daron.haar.features;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rect")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLHaar {

    int x;
    int y;
    int width;
    int height;
    @XmlAttribute
    boolean isRotated;

    public XMLHaar(int x, int y, int width, int height, boolean isRotated) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isRotated = isRotated;
    }

    public XMLHaar() {

    }
}

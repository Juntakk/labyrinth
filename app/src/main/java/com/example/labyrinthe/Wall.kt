package com.example.labyrinthe

class Wall {
    var posXStart : Int
    var posYStart : Int
    var posXEnd : Int
    var posYEnd : Int
    private var length : Int
    private var isVertical : Boolean

    constructor(posXStart:Int, posYStart:Int, posXEnd:Int, posYEnd:Int, length: Int, isVertical: Boolean){
        this.posXStart = posXStart
        this.posYStart = posYStart
        this.posXEnd = posXEnd
        this.posYEnd = posYEnd
        this.length = length
        this.isVertical = isVertical
    }
}
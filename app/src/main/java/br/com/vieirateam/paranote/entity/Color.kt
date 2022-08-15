package br.com.vieirateam.paranote.entity

import de.hdodenhof.circleimageview.CircleImageView

data class Color(
    var id: Int,
    val colorResource: Int,
    val circleImageView: CircleImageView?
)
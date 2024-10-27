package io.jcervelin.models

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ChatRoomTest {


    @Test
    fun test () {

        val mut = mutableListOf(1,2,3,4,5,6)

        val immut = mut.toList()

        println("Muttable: $mut")
        println("Immuttable: $immut")
        println("Muttable: $mut")
        println("Immuttable: $immut")


    }

}
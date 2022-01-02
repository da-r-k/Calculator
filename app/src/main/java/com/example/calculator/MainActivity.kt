package com.example.calculator

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.lang.RuntimeException
import kotlin.math.pow

fun eval(str: String): Double {
    return object : Any() {
        var pos = -1
        var ch = 0
        fun nextChar() {
            ch = if (++pos < str.length) str[pos].toInt() else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.toInt()) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
            return x
        }

        // Grammar:
        // expression = term | expression `+` term | expression `-` term
        // term = factor | term `*` factor | term `/` factor
        // factor = `+` factor | `-` factor | `(` expression `)`
        //        | number | functionName factor | factor `^` factor
        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.toInt())) x += parseTerm() // addition
                else if (eat('-'.toInt())) x -= parseTerm() // subtraction
                else return x
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.toInt())) x *= parseFactor() // multiplication
                else if (eat('/'.toInt())) x /= parseFactor() // division
                else return x
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.toInt())) return parseFactor() // unary plus
            if (eat('-'.toInt())) return -parseFactor() // unary minus
            var x: Double
            val startPos = pos
            if (eat('('.toInt())) { // parentheses
                x = parseExpression()
                eat(')'.toInt())
            } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) { // numbers
                while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // functions
                while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
                val func = str.substring(startPos, pos)
                x = parseFactor()
                x =
                    if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(
                        Math.toRadians(
                            x
                        )
                    ) else if (func == "cos") Math.cos(
                        Math.toRadians(x)
                    ) else if (func == "tan") Math.tan(Math.toRadians(x)) else throw RuntimeException(
                        "Unknown function: $func"
                    )
            } else {
                throw RuntimeException("Unexpected: " + ch.toChar())
            }
            if (eat('^'.toInt())) x = x.pow(parseFactor()) // exponentiation
            return x
        }
    }.parse()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var stbc = findViewById<TextView>(R.id.stbc)
        var ans = findViewById<TextView>(R.id.ans)
        var AC = findViewById<Button>(R.id.AC)
        var plusminus = findViewById<TextView>(R.id.plusminus)
        var percent = findViewById<TextView>(R.id.percent)
        var divide = findViewById<TextView>(R.id.divide)
        var into = findViewById<TextView>(R.id.into)
        var plus = findViewById<TextView>(R.id.plus)
        var minus = findViewById<TextView>(R.id.minus)
        var b0 = findViewById<TextView>(R.id.b0)
        var b1 = findViewById<TextView>(R.id.b1)
        var b2 = findViewById<TextView>(R.id.b2)
        var b3 = findViewById<TextView>(R.id.b3)
        var b4 = findViewById<TextView>(R.id.b4)
        var b5 = findViewById<TextView>(R.id.b5)
        var b6 = findViewById<TextView>(R.id.b6)
        var b7 = findViewById<TextView>(R.id.b7)
        var b8 = findViewById<TextView>(R.id.b8)
        var b9 = findViewById<TextView>(R.id.b9)
        var dot = findViewById<TextView>(R.id.dot)
        var equal = findViewById<TextView>(R.id.equal)
        var set = findViewById<TextView>(R.id.set)

        var s=""
        var s1=""
        var x=""
        var boo=0

        fun reset(){
            s="0"
            s1=""
            x=s
            stbc.text=""
        }

        set.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }



        AC.setOnLongClickListener {
            s=""
            s1=""
            x=s
            ans.text="0"
            stbc.text=""
            AC.text="AC"
            val handler = Handler()
            handler.postDelayed({
                AC.text="CE"
            }, 500)
            true
        }





        AC.setOnClickListener {
            s=s.dropLast(1)
            if(s==""){
                s="0"
            }
            ans.text=s
        }

        plusminus.setOnClickListener{
            if(s==""){
                s="0"
            }
            s=((s.toFloat())*-1).toString()
            ans.text=s
            stbc.text=s1
        }

        percent.setOnClickListener{
            if(s==""){
                s="0"
            }
            s=((s.toFloat())/100).toString()
            ans.text=s
            stbc.text=s1
        }

        divide.setOnClickListener{
            if(boo==1){
                s1=s1.dropLast(1)+"÷"
                x=x.dropLast(1)+"/"
            }
            else if(s==""){
                x="0/"
                s1="0÷"
            }
            else{
                s1=(eval(x+s)).toString()
                if(s1=="Infinity" || s1=="NaN"){
                    Toast.makeText(this, "INVALID INPUT - Resetting the workspace", Toast.LENGTH_SHORT).show()
                    reset()
                }
                else {
                    x = "$s1/"
                    s1 = "$s1÷"
                }
            }

            ans.text=s
            s=""
            stbc.text=s1
            boo=1
        }

        into.setOnClickListener{
            if(boo==1){
                s1=s1.dropLast(1)+"×"
                x=x.dropLast(1)+"*"
            }
            else if(s==""){
                x="0*"
                s1="0×"
            }
            else{
                s1=eval(x+s).toString()
                if(s1=="Infinity" || s1=="NaN"){
                    Toast.makeText(this, "INVALID INPUT - Resetting the workspace", Toast.LENGTH_SHORT).show()
                    reset()
                }
                else {
                    x = "$s1*"
                    s1 = "$s1×"
                }
            }

            ans.text=s
            s=""
            stbc.text=s1
            boo=1
        }

        plus.setOnClickListener{
            if(boo==1){
                s1=s1.dropLast(1)+"+"
                x=x.dropLast(1)+"+"
            }
            else if(s==""){
                x="0+"
                s1="0+"
            }
            else{
                s1=eval(x+s).toString()
                if(s1=="Infinity" || s1=="NaN"){
                    Toast.makeText(this, "INVALID INPUT - Resetting the workspace", Toast.LENGTH_SHORT).show()
                    reset()
                }
                else {
                    x = "$s1+"
                    s1 += "+"
                }
            }

            ans.text=s
            s=""
            stbc.text=s1
            boo=1
        }

        minus.setOnClickListener{
            if(boo==1){
                s1=s1.dropLast(1)+"−"
                x=x.dropLast(1)+"-"
            }
            else if(s==""){
                x="0-"
                s1="0−"
            }
            else{
                s1=eval(x+s).toString()
                if(s1=="Infinity" || s1=="NaN"){
                    Toast.makeText(this, "INVALID INPUT - Resetting the workspace", Toast.LENGTH_SHORT).show()
                    reset()
                }
                else {
                    x = "$s1-"
                    s1 = "$s1−"
                }
            }

            ans.text=s
            s=""
            stbc.text=s1
            boo=1
        }

        dot.setOnClickListener{
            if(s.filter{ it == '.' }.count()==0){
                s= "$s."
                ans.text=s
                stbc.text=s1
            }
        }

        b1.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "1"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b2.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "2"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b3.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "3"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b4.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "4"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b5.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "5"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b6.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "6"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b7.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "7"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b8.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "8"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b9.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "9"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        b0.setOnClickListener{
            if(s=="0"){
                s=""
            }
            s += "0"
            ans.text=s
            stbc.text=s1
            boo=0
        }

        equal.setOnClickListener{
            try {
                if (s == "") {
                    s = "0"
                }
                if (boo == 1) {
                    x = x.dropLast(1)
                    s = "+0"
                }
                x=eval(x+s).toString()
                s=x
                s1=""
                if(s=="Infinity" || s=="NaN"){
                    throw Exception()
                }
                ans.text=s
                stbc.text=s1
                x=""
                boo=0
                if(s=="0"){
                    s=""
                }
            }
            catch(e:Exception){
                s1=""
                s="Error"
                ans.text=s
                stbc.text=s1
                x=""
                s=""
                boo=0
            }

        }

    }
}
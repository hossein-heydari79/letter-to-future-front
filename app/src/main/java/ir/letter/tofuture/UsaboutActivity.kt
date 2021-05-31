package ir.letter.tofuture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast


class UsaboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usabout)

        Toast.makeText(this,"Description page",Toast.LENGTH_SHORT).show()

        supportActionBar?.title="درباه ی ما"

    }



}

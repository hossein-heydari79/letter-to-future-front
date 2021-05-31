package ir.letter.tofuture

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.astritveliu.boom.Boom
import com.github.florent37.kotlin.pleaseanimate.PleaseAnim
import com.github.florent37.kotlin.pleaseanimate.please
import com.thekhaeng.pushdownanim.PushDownAnim

import ir.hamsaa.persiandatepicker.Listener
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.util.PersianCalendar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject
import java.security.AccessController.getContext
import java.util.concurrent.TimeUnit



class MainActivity : AppCompatActivity() {

    val tag = "App Information's..."
    var mDay: Int? = null
    var mMonth: Int? = null
    var mYear: Int? = null

    val url = "http://lettertofuture.coolpage.biz/registerLetter.php"
    val get_data_url = "http://lettertofuture.coolpage.biz/appStatistics.php"


    var requestQueue : RequestQueue? = null
    var queue : RequestQueue? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Boom(cardView_statistics as View)
        Boom(letter_id as View)
        Boom(email_id as View)
        Boom(send_id as View)
        Boom(button_selectDate as View)
        Boom(textView5 as View)

        rainView.setImages(intArrayOf(R.drawable.mail)).start()



        PushDownAnim.setPushDownAnimTo(send_id)

        button_selectDate.setOnClickListener {

            val picker = PersianDatePickerDialog(this)
                .setPositiveButtonString("تآیید")
                .setNegativeButton("بیخیال")
                .setActionTextColor(Color.BLACK)
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(object : Listener {
                    override fun onDateSelected(persianCalendar: PersianCalendar?) {

                        val p =persianCalendar!!

                        val cc= DateConverter.jalali_to_gregorian(p.persianYear,p.persianMonth,p.persianDay)

                        mYear =cc[0]
                        mMonth =cc[1]
                        mDay = cc[2]


                        button_selectDate.text = persianCalendar.persianLongDate

                        Toast.makeText(
                            applicationContext,
                            mDay.toString() + ", " + mMonth + ", " + mYear,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onDismissed() {

                        if (mYear == null && mMonth == null && mDay == null) {
                            Toast.makeText(
                                applicationContext,
                                "Enter Your Date , Please",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                })

            picker.show()
        }

        send_id.setOnClickListener {


            if (!letter_id.text.isEmpty() && !email_id.text.isEmpty() && mYear != null && mMonth != null && mDay != null) {


                var requestQueue = Volley.newRequestQueue(this)
                val sr: StringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener {
                        if (it.contains("ok")) {
                            alertDialog("تاییدیه","  نامه ی شما با موفقیت ارسال گردید. \n \n  ایمیلی برای شما ارسال می گردد آن را بررسی کنید تا نامه ی خود را در زمان مقرر دریافت کنید . \n    در صورت مشاهده نکردن ایمیل ، پوشه ی اسپم را چک کنید. ")
                        } else {
                            alertDialog("هشدار","نامه ی شما برای ما ارسال نشده است لطفا در ارسال اطلاعات دقت نمایید.")
                        }
                    },
                    Response.ErrorListener {
                        alertDialog("خطا","  نامه ی شما برای ما ارسال نشده است. \n" +
                                " \n" +
                                " مشکلی در سرور به وجود آمده است لطفا دقایقی بعد دوباره تلاش کنید.")
                    }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> =
                            HashMap()
                        params["letter"] = letter_id.text.toString()
                        params["email"] = email_id.text.toString()
                        params["year"] = mYear.toString()
                        params["month"] = mMonth.toString()
                        params["day"] = mDay.toString()
                        return params
                    }

                }
                requestQueue?.add(sr)
            }else{
                Toast.makeText(this,"Enter Your Information , Please",Toast.LENGTH_SHORT).show()
            }
        }


        var request = Volley.newRequestQueue(this)
        var jsonObj = JsonObjectRequest(
            Request.Method.GET,
            get_data_url,
            Response.Listener {
                    response: JSONObject ->

                val a=response.getString("to_be_lettered")
                val b=response.getString("lettered")
                val c=response.getString("all_registered_letters")

                appStatistics = arrayOf(" تعداد نامه هایی که در آینده ارسال خواهند شد : $a" , " تعداد نامه هایی که تا الان ارسال شده اند : $b" , " تعداد کل نامه های ثبت شده در برنامه : $c")

                cardView_statistics.visibility=View.VISIBLE

                anim()

            },
            Response.ErrorListener {
                error: VolleyError? ->
                error?.printStackTrace()
            }
        )
        request?.add(jsonObj)




    }

    var i =0
    lateinit var appStatistics:Array<String>

    private fun anim ()
    {

        please(duration = 350L) {
            animate(textView_appStatistics) toBe {
                visible()
            }
        }.withEndAction {

            please(duration = 350L) {
                animate(textView_appStatistics) toBe{
                    invisible()
                }
            }.setStartDelay(3000L).withEndAction {
                textView_appStatistics.text = appStatistics[i]

                i += 1
                if( i == 3){
                    i = 0
                }

                anim()

            }.start()

        }.start()


    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        if (item?.itemId == R.id.us_about) {
            startActivity(Intent(this, UsaboutActivity::class.java))
        }
        return true
    }


    fun alertDialog(title: String, msg: String) {

        val mAlertDialog = AlertDialog.Builder(this)

        mAlertDialog.setTitle(title)
        mAlertDialog.setMessage(msg)

        mAlertDialog.setNegativeButton("باشه"){dialog, id ->
            dialog.dismiss()
        }

        mAlertDialog.show()

    }


}









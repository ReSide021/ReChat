package com.example.myapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.myapplication.ActivityMain.Companion.sqliteHelper
import com.example.myapplication.ActivityMain.Companion.webSocketClient
import com.example.myapplication.dataClasses.ActionsWithFrnd
import com.example.myapplication.dataClasses.ConfirmAddFriend
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FriendsProfile : AppCompatActivity() {

    private lateinit var tagUser : String
    private lateinit var sp : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_profile)
        sp = getSharedPreferences("OURINFO", Context.MODE_PRIVATE)
        val nameOfUserView = findViewById<TextView>(R.id.nameOfUser)
        val tagUserView = findViewById<TextView>(R.id.tagOfUser)
        val userInFriendsBtn = findViewById<Button>(R.id.userInFriend)
        tagUser = intent.extras?.getString("idTag").toString()
        val nameOfUser = intent.extras?.getString("nameOfUser").toString()

        val statusThisUser = sqliteHelper.getStatusUser(tagUser)
        when (statusThisUser) {
            -1 -> {

            }
            0 -> {
                userInFriendsBtn.text = "Принять запрос"
            }
            1 -> {
                userInFriendsBtn.text = "Ожидает подтверждения"
            }
            2 -> {
                userInFriendsBtn.text = "Удалить из друзей"
                userInFriendsBtn.backgroundTintList = AppCompatResources.getColorStateList(this, R.color.green)
            }
        }

        nameOfUserView.text = nameOfUser
        tagUserView.text = tagUser

        supportActionBar?.apply {
            title = nameOfUser
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        val urlAvatar = "http://imagerc.ddns.net:80/avatar/avatarImg/$tagUser.jpg"
        val imageOfUser = findViewById<ImageView>(R.id.imageOfUser)
        Picasso.get()
            .load(urlAvatar)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
            .placeholder(R.drawable.user_profile_photo)
            .into(imageOfUser)

        findViewById<ImageButton>(R.id.copyTagBtn).setOnClickListener {
            try {
                val clipboard: ClipboardManager? =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("TAG", tagUser)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(
                    this, resources.getString(R.string.copy_tag),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (ex: Exception){}
        }
        userInFriendsBtn.setOnClickListener {
            if(webSocketClient.connection.readyState.ordinal == 0){
                Toast.makeText(
                    this, "Отсутствует подключение к серверу",
                    Toast.LENGTH_SHORT
                ).show()
            } else{
                when (statusThisUser) {
                    -1 -> {
                        val dataUser = ActionsWithFrnd("FRND::", "ADD::", tagUser, nameOfUser)
                        val msg = Json.encodeToString(dataUser)
                        webSocketClient.send(msg)
                    }
                    0 -> {
                        val dataUser = ConfirmAddFriend("FRND::", "CNFRMADD::", tagUser)
                        val msg = Json.encodeToString(dataUser)
                        webSocketClient.send(msg)
                    }
                    1 -> {
                        return@setOnClickListener
                    }
                    2 -> {
                        val dataUser = ConfirmAddFriend("FRND::", "DELETE::", tagUser)
                        val msg = Json.encodeToString(dataUser)
                        webSocketClient.send(msg)
                    }
                }
                onBackPressed()
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
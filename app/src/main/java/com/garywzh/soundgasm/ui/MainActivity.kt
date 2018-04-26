package com.garywzh.soundgasm.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.garywzh.soundgasm.Constants
import com.garywzh.soundgasm.PreferenceHelper.defaultPrefs
import com.garywzh.soundgasm.PreferenceHelper.get
import com.garywzh.soundgasm.PreferenceHelper.set
import com.garywzh.soundgasm.R
import com.garywzh.soundgasm.model.ArtistRepo
import com.garywzh.soundgasm.model.Sound
import com.garywzh.soundgasm.model.SoundRepo
import com.garywzh.soundgasm.network.NetworkObj
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.indefiniteSnackbar
import org.jetbrains.anko.design.longSnackbar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Future

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = defaultPrefs(this)
        val init = prefs[Constants.PREFERENCE_KEY_INIT, true]
        loadData(init ?: true)
    }

    private fun loadData(fromNetwork: Boolean) {
        val snackBar = if (fromNetwork)
            indefiniteSnackbar(viewGroup, "Indexing, please wait...") else null
        doAsync {
            val list = if (fromNetwork) loadFromNetwork() else loadJsonFromSdcard()
            SoundRepo.sounds = list
            uiThread {
                snackBar?.let {
                    it.dismiss()
                    longSnackbar(viewGroup, "${list.size} audios indexed.")
                }
                (fragment as SoundListFragment).setData(list)
            }
        }
    }

    private fun showFilteredData(search: String) {
        SoundRepo.sounds?.let {
            doAsync {
                val list = it.filter {
                    it.artist.contains(search, true)
                            || it.title.contains(search, true)
                            || it.description.contains(search, true)
                }
                uiThread {
                    (fragment as SoundListFragment).setData(list)
                }
            }
        }
    }

    private fun loadJsonFromSdcard(): List<Sound> {
        val path = application.getExternalFilesDir(null)
        val file = File(path, "sounds.json")
        val jsonString = FileInputStream(file).bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonString, object : TypeToken<List<Sound>>() {}.type)
    }

    private fun loadFromNetwork(): List<Sound> {
        val futureList = mutableListOf<Future<List<Sound>?>>()
        for (name in ArtistRepo.artists) {
            futureList.add(doAsyncResult { NetworkObj.getSoundList(name) })
        }
        val list = mutableListOf<Sound>()
        for (future in futureList) {
            future.get()?.let { list.addAll(it) }
        }
        list.sortBy { -it.playCount }
        writeJsonToSdcard(Gson().toJson(list))
        return list
    }

    private fun writeJsonToSdcard(json: String) {
        val path = application.getExternalFilesDir(null)
        val file = File(path, "sounds.json")
        file.delete()
        FileOutputStream(file, false).use {
            it.write(json.toByteArray())
        }
        val prefs = defaultPrefs(this)
        prefs[Constants.PREFERENCE_KEY_INIT] = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.let {
            val searchItem = it.findItem(R.id.action_search)
            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    SoundRepo.sounds?.let {
                        (fragment as SoundListFragment).setData(it)
                    }
                    return true
                }
            })
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        if (query.isNotEmpty()) {
                            searchView.clearFocus()
                            showFilteredData(query)
                            return true
                        }
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        SoundRepo.sounds?.let {
                            (fragment as SoundListFragment).setData(it)
                        }
                    }
                    return false
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            return when (it.itemId) {
                R.id.action_all_artists -> {
                    startActivity<ArtistListActivity>()
                    true
                }
                R.id.action_update_database -> {
                    loadData(true)
                    true
                }
                R.id.action_belle -> {
                    browse("https://www.reddit.com/r/Belle_in_the_woods/comments/52o3zx/erotic_audios/")
                    true
                }
                R.id.action_petalbaby -> {
                    browse("https://www.reddit.com/r/gonewildaudio/comments/7qmer1/f4m_12_little_girl_audios_age_teen_ddlg_petals/")
                    true
                }
                else -> false
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
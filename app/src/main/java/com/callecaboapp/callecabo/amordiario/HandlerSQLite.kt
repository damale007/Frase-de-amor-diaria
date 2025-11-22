package com.callecaboapp.callecabo.amordiario

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Creado por damale on 25/07/2024.
 */
class HandlerSQLite(ctx: Context?) : SQLiteOpenHelper(ctx, "cache.db", null, 1) {
    private val favoritosSQL = "CREATE TABLE favoritos (id INTEGER PRIMARY KEY AUTOINCREMENT, indice INTEGER, texto TEXT)"
    private var c: Cursor? = null
    private var database: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(favoritosSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        db.execSQL("DROP TABLE IF EXISTS favoritos")

        db.execSQL(favoritosSQL)
    }

    fun inicia(db: SQLiteDatabase?) {
        database = db
    }

    fun count(): Int {
        var cantidad: Int

        if (database != null) {
            try {
                c = database!!.rawQuery("SELECT * FROM favoritos", null)
                cantidad = c!!.count
            } catch (e: Exception) {
                Log.e("ERROR SQL", "Error en count: $e")
                cantidad = 0
            }
        } else cantidad = 0

        return cantidad
    }

    fun insertFavoritos(indice: Int, texto: String) {
        try {
            database!!.execSQL("INSERT INTO favoritos (indice, texto) VALUES ('$indice', '$texto')")
        } catch (e: Exception) {
            Log.e("ERROR", "Inserta grupo evento: $e")
        }
    }

    fun delete(cnd: String) {
        //Eliminar un registro
        var condicion = cnd
        try {
            if (condicion != "") condicion = " WHERE $condicion"
            database!!.execSQL("DELETE FROM favoritos$condicion")
        } catch (e: Exception) {
            Log.e("ERROR", "Error al borrar tabla: $e")
        }
    }

    fun select(bsc: String?): Array<Array<String?>>? {
        var busca = bsc
        var datos: Array<Array<String?>>?

        busca = if (busca != null) " WHERE $busca" else ""
        val sql = "SELECT * FROM favoritos $busca"

        c = database!!.rawQuery(sql, null)
        c!!.moveToFirst()

        val cantidad = c!!.count
        datos = Array(cantidad) { arrayOfNulls(3) }

        try {
            if (cantidad > 0) {
                for (i in 0 until cantidad) {
                    for (j in 0..2) datos[i][j] = c!!.getString(j)

                    c!!.moveToNext()
                }
            } else datos = null
        } catch (e: Exception) {
            datos = null
        }
        return datos
    }

    fun cierra(database: SQLiteDatabase) {
        database.close()
    }

}

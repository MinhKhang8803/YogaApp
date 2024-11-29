package com.example.yoganativeapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.UUID

class YogaDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "YogaNativeApp.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_YOGA_CLASSES = "YogaClasses"
        const val TABLE_CLASS_INSTANCES = "ClassInstances"

        const val COLUMN_ID = "id"
        const val COLUMN_DAY_OF_WEEK = "dayOfWeek"
        const val COLUMN_TIME = "time"
        const val COLUMN_CAPACITY = "capacity"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_PRICE = "price"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DESCRIPTION = "description"

        const val COLUMN_INSTANCE_ID = "instanceId"
        const val COLUMN_CLASS_ID = "classId"
        const val COLUMN_DATE = "date"
        const val COLUMN_TEACHER = "teacher"
        const val COLUMN_COMMENTS = "comments"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createYogaClassesTable = """
            CREATE TABLE $TABLE_YOGA_CLASSES (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_DAY_OF_WEEK TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_CAPACITY INTEGER,
                $COLUMN_DURATION INTEGER,
                $COLUMN_PRICE REAL,
                $COLUMN_TYPE TEXT,
                $COLUMN_DESCRIPTION TEXT
            )
        """
        db.execSQL(createYogaClassesTable)

        val createClassInstancesTable = """
            CREATE TABLE $TABLE_CLASS_INSTANCES (
                $COLUMN_INSTANCE_ID TEXT PRIMARY KEY,
                $COLUMN_CLASS_ID TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TEACHER TEXT,
                $COLUMN_COMMENTS TEXT,
                FOREIGN KEY($COLUMN_CLASS_ID) REFERENCES $TABLE_YOGA_CLASSES($COLUMN_ID)
            )
        """
        db.execSQL(createClassInstancesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_YOGA_CLASSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS_INSTANCES")
        onCreate(db)
    }

    fun addYogaClass(yogaClass: YogaClass): Long {
        val db = this.writableDatabase
        val id = yogaClass.id ?: UUID.randomUUID().toString()
        val values = ContentValues().apply {
            put(COLUMN_ID, id)
            put(COLUMN_DAY_OF_WEEK, yogaClass.dayOfWeek)
            put(COLUMN_TIME, yogaClass.time)
            put(COLUMN_CAPACITY, yogaClass.capacity)
            put(COLUMN_DURATION, yogaClass.duration)
            put(COLUMN_PRICE, yogaClass.price)
            put(COLUMN_TYPE, yogaClass.type)
            put(COLUMN_DESCRIPTION, yogaClass.description)
        }
        return db.insert(TABLE_YOGA_CLASSES, null, values).also { db.close() }
    }

    fun updateYogaClass(yogaClass: YogaClass): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DAY_OF_WEEK, yogaClass.dayOfWeek)
            put(COLUMN_TIME, yogaClass.time)
            put(COLUMN_CAPACITY, yogaClass.capacity)
            put(COLUMN_DURATION, yogaClass.duration)
            put(COLUMN_PRICE, yogaClass.price)
            put(COLUMN_TYPE, yogaClass.type)
            put(COLUMN_DESCRIPTION, yogaClass.description)
        }
        return db.update(TABLE_YOGA_CLASSES, values, "$COLUMN_ID=?", arrayOf(yogaClass.id)).also {
            db.close()
        }
    }

    fun getAllYogaClasses(): List<YogaClass> {
        val yogaClasses = mutableListOf<YogaClass>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_YOGA_CLASSES", null)

        if (cursor.moveToFirst()) {
            do {
                val yogaClass = YogaClass(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                    capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                    duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                yogaClasses.add(yogaClass)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return yogaClasses
    }

    fun getAllClassInstances(): List<ClassInstance> {
        val db = readableDatabase
        val cursor = db.query(
            "ClassInstances",
            null, null, null, null, null, null
        )
        val instances = mutableListOf<ClassInstance>()

        while (cursor.moveToNext()) {
            val instance = ClassInstance(
                id = cursor.getString(cursor.getColumnIndexOrThrow("instanceId")),
                yogaClassId = cursor.getString(cursor.getColumnIndexOrThrow("classId")),
                date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher")),
                comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"))
            )
            instances.add(instance)
        }

        cursor.close()
        return instances
    }



    fun deleteYogaClass(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_YOGA_CLASSES, "$COLUMN_ID=?", arrayOf(id)).also { db.close() }
    }

    fun addClassInstance(classInstance: ClassInstance): Long {
        val db = this.writableDatabase
        val id = classInstance.id ?: UUID.randomUUID().toString()
        val values = ContentValues().apply {
            put(COLUMN_INSTANCE_ID, id)
            put(COLUMN_CLASS_ID, classInstance.yogaClassId)
            put(COLUMN_DATE, classInstance.date)
            put(COLUMN_TEACHER, classInstance.teacher)
            put(COLUMN_COMMENTS, classInstance.comments)
        }
        return db.insert(TABLE_CLASS_INSTANCES, null, values).also { db.close() }
    }

    fun getClassInstances(classId: String): List<ClassInstance> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CLASS_INSTANCES,
            null,
            "$COLUMN_CLASS_ID=?",
            arrayOf(classId),
            null, null, null
        )
        val classInstances = mutableListOf<ClassInstance>()
        cursor?.use {
            while (cursor.moveToNext()) {
                val instance = ClassInstance(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID)),
                    yogaClassId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER)),
                    comments = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS))
                )
                classInstances.add(instance)
            }
        }
        db.close()
        return classInstances
    }

    fun deleteClassInstance(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_CLASS_INSTANCES, "$COLUMN_INSTANCE_ID=?", arrayOf(id)).also { db.close() }
    }
}
package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.TagTeamDao
import com.example.data.local.entity.CloudSyncEntity
import com.example.data.local.entity.DlcExpansionEntity
import com.example.data.local.entity.IsoGameEntity
import com.example.data.local.entity.SaveGameEntity

@Database(
    entities = [
        IsoGameEntity::class,
        DlcExpansionEntity::class,
        SaveGameEntity::class,
        CloudSyncEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TagTeamDatabase : RoomDatabase() {

    abstract fun tagTeamDao(): TagTeamDao

    companion object {
        @Volatile
        private var INSTANCE: TagTeamDatabase? = null

        fun getDatabase(context: Context): TagTeamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TagTeamDatabase::class.java,
                    "tag_team_port_hub.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

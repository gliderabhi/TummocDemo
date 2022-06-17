package com.example.tummocduplicate.bean

import androidx.room.*

@Dao
interface TummocDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTummocData(tummonJson: TummocBaseJson)

    @Query("Select * from TummocBaseJson")
    suspend fun getTummocData(): TummocBaseJson?

    @Delete
    suspend fun deleteTummocData(tummocBaseJson: TummocBaseJson)
}
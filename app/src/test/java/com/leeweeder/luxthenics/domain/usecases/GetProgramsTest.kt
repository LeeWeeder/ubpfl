/*
package com.leeweeder.luxthenics.domain.usecases

import com.leeweeder.luxthenics.data.repository.FakeProgramRepository
import com.leeweeder.luxthenics.api_program.data.source.Program
import com.leeweeder.luxthenics.domain.usecases.program.GetPrograms
import kotlinx.coroutines.runBlocking
import org.junit.Before

class GetNotesTest {

    private lateinit var getPrograms: GetPrograms
    private lateinit var fakeRepository: FakeProgramRepository

    @Before
    fun setUp() {
        fakeRepository = FakeProgramRepository()
        getPrograms = GetPrograms(fakeRepository)

        val programsToInsert = mutableListOf<Program>()
        ('a'..'z').forEachIndexed { _, c ->
            programsToInsert.add(
                Program(
                    name = c.toString()
                )
            )
        }
        programsToInsert.shuffle()
        runBlocking {
            programsToInsert.forEach { fakeRepository.upsertProgram(it) }
        }
    }
}*/

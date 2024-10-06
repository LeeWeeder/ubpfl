/*
package com.leeweeder.yluxthenics.data.repository

import com.leeweeder.yluxthenics.api_program.data.source.Program
import com.leeweeder.yluxthenics.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeProgramRepository @Inject constructor() : ProgramRepository {
    private val programs = mutableListOf<Program>()

    override fun getPrograms(): Flow<List<Program>> {
        return flow { emit(programs) }
    }

    override suspend fun getProgramById(id: Int): Program {
        return programs.find { it.id == id }!!
    }

    override suspend fun upsertProgram(program: Program) {
        if (program.id !in 0..programs.size) {
            programs.add(program)
        } else {
            programs[program.id] = program
        }
    }

    override suspend fun deleteProgram(program: Program) {
        programs.remove(program)
    }
}*/

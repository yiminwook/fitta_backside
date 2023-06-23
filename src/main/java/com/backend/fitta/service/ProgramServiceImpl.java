package com.backend.fitta.service;

import com.backend.fitta.entity.gym.Program;
import com.backend.fitta.entity.staff.Staff;
import com.backend.fitta.repository.program.ProgramRepository;
import com.backend.fitta.service.interfaces.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    @Override
    public Long save(Program program) {
        Program savedProgram = programRepository.save(program);
        return savedProgram.getId();
    }

    @Override
    public Program findById(Long id) {
        return programRepository.findById(id).orElseThrow(RuntimeException::new);//예외처리필
    }

    @Override
    public List<Program> findAll() {
        return programRepository.findAll();
    }

    @Override
    public Long update(Long id, Program program) {
        return null;
    }

    @Override
    public void delete(Long id) {
        programRepository.delete(findById(id));
    }
}

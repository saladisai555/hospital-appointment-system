package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.dto.request.DepartmentRequest;
import com.example.hospital_appointment_system.dto.response.DepartmentResponse;
import com.example.hospital_appointment_system.entity.Department;
import com.example.hospital_appointment_system.exception.ConflictException;
import com.example.hospital_appointment_system.exception.ResourceNotFoundException;
import com.example.hospital_appointment_system.mapper.DepartmentMapper;
import com.example.hospital_appointment_system.repository.DepartmentRepository;
import com.example.hospital_appointment_system.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream()
                .map(DepartmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Integer id) {
        return DepartmentMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new ConflictException("A department with this name already exists");
        }
        Department department = new Department();
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setActive(request.getActive() == null || request.getActive());
        return DepartmentMapper.toResponse(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Integer id, DepartmentRequest request) {
        Department department = findEntity(id);
        if (!department.getName().equalsIgnoreCase(request.getName())
                && departmentRepository.existsByName(request.getName())) {
            throw new ConflictException("A department with this name already exists");
        }
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        if (request.getActive() != null) {
            department.setActive(request.getActive());
        }
        return DepartmentMapper.toResponse(department);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // Soft delete: a hard DELETE would break FK history on existing doctors/appointments
        findEntity(id).setActive(false);
    }

    private Department findEntity(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }
}
package com.upm.institutional.service;

import com.upm.institutional.dto.CourseDto;
import com.upm.institutional.model.Course;
import com.upm.institutional.model.Status;
import com.upm.institutional.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<Course> findPublishedCourses(Pageable pageable) {
        return courseRepository.findByStatus(Status.PUBLISHED, pageable);
    }

    public Page<Course> findAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    public Course findPublishedById(Long id) {
        Course course = findById(id);
        if (course.getStatus() != Status.PUBLISHED) {
            throw new RuntimeException("Curso no disponible");
        }
        return course;
    }

    @Transactional
    public void saveCourse(CourseDto courseDto) {
        Course course = new Course();
        if (courseDto.getId() != null) {
            course = findById(courseDto.getId());
        } else {
            course.setStatus(Status.DRAFT); // Default for new
        }
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setModality(courseDto.getModality());
        course.setDuration(courseDto.getDuration());
        course.setStartDate(courseDto.getStartDate());
        if (courseDto.getStatus() != null) {
            course.setStatus(courseDto.getStatus());
        }
        course.setImageUrl(courseDto.getImageUrl());
        courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Transactional
    public void changeStatus(Long id, Status status) {
        Course course = findById(id);
        course.setStatus(status);
        courseRepository.save(course);
    }

    public CourseDto toDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setModality(course.getModality());
        dto.setDuration(course.getDuration());
        dto.setStartDate(course.getStartDate());
        dto.setStatus(course.getStatus());
        dto.setImageUrl(course.getImageUrl());
        return dto;
    }
}

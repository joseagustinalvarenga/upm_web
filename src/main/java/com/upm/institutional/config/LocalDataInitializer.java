package com.upm.institutional.config;

import com.upm.institutional.model.*;
import com.upm.institutional.repository.CourseRepository;
import com.upm.institutional.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class LocalDataInitializer implements CommandLineRunner {

    private final NewsRepository newsRepository;
    private final CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting local mock data initialization...");

        if (newsRepository.count() == 0) {
            log.info("Seeding news articles...");
            
            News news1 = new News();
            news1.setTitle("Comienza el año electivo 2026");
            news1.setContent("Las capacitaciones abarcan áreas como plomería, costura, gastronomía, herrería, electricidad y otros oficios vinculados a la producción y los servicios, pensados para responder a las demandas sociales y productivas de la provincia.\n\nMás allá de la enseñanza técnica, los talleres fomentan el trabajo colaborativo, la autonomía y el emprendedurismo, con salidas laborales concretas, emprendimientos y mayor inserción en el mercado de trabajo.\n\nLa UPM cuenta con sedes permanentes en Posadas y en más de 35 municipios, ofrece más de 60 oficios sin costo y prepara el inicio del ciclo lectivo 2026 según la demanda de cada localidad.");
            news1.setImageUrl("https://images.unsplash.com/photo-1513258496099-48168024aec0?q=80&w=2070&auto=format&fit=crop");
            news1.setEventDate(LocalDate.of(2026, 2, 3));
            news1.setStatus(Status.PUBLISHED);
            news1.setCreatedAt(LocalDateTime.now().minusDays(10));
            newsRepository.save(news1);

            News news2 = new News();
            news2.setTitle("Inauguración de la nueva sede en Oberá");
            news2.setContent("Con la presencia de autoridades provinciales y municipales, se inauguró oficialmente la nueva sede de la Universidad Popular de Misiones en la ciudad de Oberá.\n\nEsta nueva infraestructura permitirá duplicar la oferta de cursos y talleres de oficios en la zona centro de la provincia, respondiendo a una demanda histórica de los vecinos.\n\nLos cursos comenzarán el próximo mes con especialidades en informática, carpintería y refrigeración.");
            news2.setImageUrl("https://images.unsplash.com/photo-1541339907198-e08756dedf3f?q=80&w=2070&auto=format&fit=crop");
            news2.setEventDate(LocalDate.of(2026, 3, 10));
            news2.setStatus(Status.PUBLISHED);
            news2.setCreatedAt(LocalDateTime.now().minusDays(5));
            newsRepository.save(news2);

            News news3 = new News();
            news3.setTitle("Entrega de certificados en Eldorado");
            news3.setContent("Más de doscientos alumnos recibieron sus diplomas de finalización de cursos de oficios en un emotivo acto realizado en el polideportivo municipal de Eldorado.\n\nLos egresados completaron capacitaciones en costura industrial, auxiliar de cocina y electricidad domiciliaria.\n\nEl rector de la institución destacó el esfuerzo y la dedicación de cada estudiante durante el trayecto formativo.");
            news3.setImageUrl("https://images.unsplash.com/photo-1523240795612-9a054b0db644?q=80&w=2070&auto=format&fit=crop");
            news3.setEventDate(LocalDate.of(2026, 5, 20));
            news3.setStatus(Status.PUBLISHED);
            news3.setCreatedAt(LocalDateTime.now().minusDays(2));
            newsRepository.save(news3);
            
            log.info("News articles seeded successfully.");
        }

        if (courseRepository.count() == 0) {
            log.info("Seeding courses...");

            Course course1 = new Course();
            course1.setTitle("Instalador Electricista Domiciliario");
            course1.setDescription("Aprende a realizar instalaciones eléctricas residenciales bajo normas de seguridad vigentes.");
            course1.setModality(Modality.PRESENCIAL);
            course1.setDuration("4 meses");
            course1.setStartDate(LocalDate.of(2026, 8, 1));
            course1.setStatus(Status.PUBLISHED);
            course1.setImageUrl("https://images.unsplash.com/photo-1621905251189-08b45d6a269e?q=80&w=2069&auto=format&fit=crop");
            courseRepository.save(course1);

            Course course2 = new Course();
            course2.setTitle("Corte y Confección Básica");
            course2.setDescription("Taller práctico sobre moldería, corte y confección de prendas textiles esenciales.");
            course2.setModality(Modality.PRESENCIAL);
            course2.setDuration("3 meses");
            course2.setStartDate(LocalDate.of(2026, 8, 10));
            course2.setStatus(Status.PUBLISHED);
            course2.setImageUrl("https://images.unsplash.com/photo-1524292332607-d557a2ab814e?q=80&w=2070&auto=format&fit=crop");
            courseRepository.save(course2);

            Course course3 = new Course();
            course3.setTitle("Programación Web Front-End");
            course3.setDescription("Introducción al desarrollo web moderno utilizando HTML, CSS y JavaScript.");
            course3.setModality(Modality.VIRTUAL);
            course3.setDuration("5 meses");
            course3.setStartDate(LocalDate.of(2026, 9, 1));
            course3.setStatus(Status.PUBLISHED);
            course3.setImageUrl("https://images.unsplash.com/photo-1547658719-da2b51169166?q=80&w=2064&auto=format&fit=crop");
            courseRepository.save(course3);

            log.info("Courses seeded successfully.");
        }
        
        log.info("Local mock data initialization complete!");
    }
}

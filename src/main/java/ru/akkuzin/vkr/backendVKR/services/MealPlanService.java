package ru.akkuzin.vkr.backendVKR.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.dto.MealPlanDTO;
import ru.akkuzin.vkr.backendVKR.model.Mealplan;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.repositories.MealPlanRepository;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;
import ru.akkuzin.vkr.backendVKR.repositories.ReceptRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealPlanService {
    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private ReceptRepository receptRepository;

    @Autowired
    private PeopleRepository personRepository;

    @Transactional
    public void addToMealPlan(MealPlanDTO mealPlanDTO) {
        Recept recept = receptRepository.findById(mealPlanDTO.getReceptId())
                .orElseThrow(() -> new EntityNotFoundException("Рецепт не найден"));

        // Ищем по email вместо ID
        Person person = personRepository.findByEmail(mealPlanDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Mealplan mealPlan = new Mealplan();
        mealPlan.setDate(mealPlanDTO.getDate());
        mealPlan.setMealType(mealPlanDTO.getMealType());
        mealPlan.setRecept(recept);
        mealPlan.setPerson(person);

        mealPlanRepository.save(mealPlan);
    }


    @Transactional
    public void saveOrUpdateMealPlan(MealPlanDTO dto) {
        // 1. Находим существующую запись (если есть)
        Optional<Mealplan> existingPlan = mealPlanRepository.findByDateAndMealTypeAndPersonId(
                dto.getDate(),
                dto.getMealType(),
                personRepository.findByEmail(dto.getEmail())
                        .orElseThrow().getId()
        );

        // 2. Если запись существует - обновляем, иначе создаём новую
        Mealplan plan = existingPlan.orElse(new Mealplan());

        plan.setDate(dto.getDate());
        plan.setMealType(dto.getMealType());
        plan.setRecept(receptRepository.findById(dto.getReceptId()).orElseThrow());
        plan.setPerson(personRepository.findByEmail(dto.getEmail()).orElseThrow());

        mealPlanRepository.save(plan);
    }

    public List<MealPlanDTO> getMealPlanByDate(LocalDate date, String email) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return mealPlanRepository.findByDateAndPersonId(date, (long)person.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFromMealPlan(Long id) {
        mealPlanRepository.deleteById(id);
    }

    private MealPlanDTO convertToDTO(Mealplan mealPlan) {
        MealPlanDTO dto = new MealPlanDTO();
        dto.setId(mealPlan.getId());
        dto.setDate(mealPlan.getDate());
        dto.setMealType(mealPlan.getMealType());
        dto.setReceptId(mealPlan.getRecept().getId());
        dto.setPersonId(mealPlan.getPerson().getId());
        return dto;
    }
}
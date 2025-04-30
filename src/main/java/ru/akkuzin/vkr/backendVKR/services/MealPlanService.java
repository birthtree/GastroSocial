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
    private ReceptService receptService;

    @Autowired
    private PeopleRepository personRepository;

    @Transactional
    public void addToMealPlan(MealPlanDTO mealPlanDTO) {
        Recept recept = receptRepository.findById(mealPlanDTO.getReceptId())
                .orElseThrow(() -> new EntityNotFoundException("Рецепт не найден"));

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
        // 1. Поиск рецепта с проверкой
        Recept recept = receptRepository.findById(dto.getReceptId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Рецепт с ID %d не найден", dto.getReceptId())));

        // 2. Поиск пользователя
        Person person = personRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с email %s не найден", dto.getEmail())));

        // 3. Поиск или создание плана питания
        Mealplan plan = mealPlanRepository
                .findByDateAndMealTypeAndPersonId(dto.getDate(), dto.getMealType(), person.getId())
                .orElse(new Mealplan());

        // 4. Установка свойств
        plan.setDate(dto.getDate());
        plan.setMealType(dto.getMealType());
        plan.setRecept(recept);
        plan.setPerson(person);

        // 5. Сохранение
        mealPlanRepository.save(plan);
    }

    public List<MealPlanDTO> getMealPlanByDate(LocalDate date, String email) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return mealPlanRepository.findByDateAndPersonId(date, person.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFromMealPlan(int id) {
        if (!mealPlanRepository.existsById(id)) {
            throw new EntityNotFoundException("Запись плана питания не найдена");
        }
        mealPlanRepository.deleteById(id);
    }

    private MealPlanDTO convertToDTO(Mealplan mealPlan) {
        MealPlanDTO dto = new MealPlanDTO();
        dto.setId(mealPlan.getId());
        dto.setDate(mealPlan.getDate());
        dto.setMealType(mealPlan.getMealType());
        dto.setReceptId(mealPlan.getRecept().getId());
        dto.setPersonId(mealPlan.getPerson().getId());
        dto.setEmail(mealPlan.getPerson().getEmail());
        return dto;
    }
}
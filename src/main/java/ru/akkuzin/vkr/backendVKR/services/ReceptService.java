package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Ingredient;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.repositories.ReceptRepository;
import ru.akkuzin.vkr.backendVKR.util.PersonNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReceptService {
    private final ReceptRepository receptRepository;
    private final IngredientService ingredientService;
    @Autowired
    public ReceptService(ReceptRepository receptRepository, IngredientService ingredientService) {
        this.receptRepository = receptRepository;
        this.ingredientService = ingredientService;
    }

    public List<Recept> findAll() {
        return receptRepository.findAll();
    }

    public Recept findById(int id) {
           Optional<Recept> recept = receptRepository.findById(id);
        return recept.orElseThrow(ReceptNotFoundException::new);
    }



    @Transactional
    public void save(Recept recept) {
        if (recept.getIngredientNames() != null) {
            Set<Ingredient> ingredients = recept.getIngredientNames().stream()
                    .map(name -> ingredientService.getOrCreateIngredientByName(name.trim()))
                    .collect(Collectors.toSet()); // <-- важно: TO SET

            recept.setIngredients(ingredients);
        }

        receptRepository.save(recept);
    }
    @Transactional
    public void update(int id, Recept updatedRecept) {
        Recept receptFromDB = findById(id); // Используем существующий метод поиска

        // Обновляем основные поля
        receptFromDB.setName(updatedRecept.getName());
        receptFromDB.setDiscription(updatedRecept.getDiscription());
        receptFromDB.setDuration(updatedRecept.getDuration());
        receptFromDB.setPrivate(updatedRecept.isPrivate());
        receptFromDB.setOwner(updatedRecept.getOwner());

        // Обработка ингредиентов (аналогично save)
        if (updatedRecept.getIngredientNames() != null) {
            Set<Ingredient> ingredients = updatedRecept.getIngredientNames().stream()
                    .map(name -> ingredientService.getOrCreateIngredientByName(name.trim()))
                    .collect(Collectors.toSet());

            // Очищаем старые и устанавливаем новые ингредиенты
            receptFromDB.getIngredients().clear();
            receptFromDB.getIngredients().addAll(ingredients);
        }

        receptRepository.save(receptFromDB);
    }
    @Transactional
    public void deleteById(int id) {
        if (!receptRepository.existsById(id)) {
            throw new ReceptNotFoundException();
        }
        receptRepository.deleteById(id);
    }

}

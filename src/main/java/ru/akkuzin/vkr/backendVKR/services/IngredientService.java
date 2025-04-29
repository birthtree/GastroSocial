package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Ingredient;
import ru.akkuzin.vkr.backendVKR.repositories.IngredientRepository;
import ru.akkuzin.vkr.backendVKR.util.Ingredient.IngredientNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.PersonNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;


    @Transactional
    public Ingredient findOrCreate(String name) {
        return ingredientRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(name);
                    return ingredientRepository.save(ingredient);
                });
    }

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;

    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(int id) {
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        return ingredient.orElseThrow(IngredientNotFoundException::new);
    }
    @Transactional
    public void save(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }


    @Transactional
    public void update(int id, Ingredient ingredient) {
        Ingredient ingredientFromDB = getIngredientById(id);
        ingredientFromDB.setName(ingredient.getName());
        ingredientRepository.save(ingredientFromDB);
    }

    @Transactional
    public void deleteById(int id) {
        if (!ingredientRepository.existsById(id)) {
            throw new IngredientNotFoundException();
        }
        ingredientRepository.deleteById(id);
    }
    public Ingredient getOrCreateIngredientByName(String name) {
        String normalizedName = name.trim().toLowerCase();
        return ingredientRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(normalizedName);
                    return ingredientRepository.save(newIngredient);
                });
    }

}

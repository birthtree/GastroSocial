package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Filters;
import ru.akkuzin.vkr.backendVKR.repositories.FilterRepository;
import ru.akkuzin.vkr.backendVKR.util.Filter.FilterNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.Ingredient.IngredientNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class FilterService {
    private final FilterRepository filterRepository;

    @Autowired
    public FilterService(FilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    public List<Filters> getAllFilters() {
        return filterRepository.findAll();
    }

    public Filters getFilterById(int id) {
        return filterRepository.findById(id)
                .orElseThrow(FilterNotFoundException::new);
    }





    @Transactional
    public void deleteById(int id) {
        if (!filterRepository.existsById(id)) {
            throw new IngredientNotFoundException();
        }
        filterRepository.deleteById(id);
    }

    @Transactional
    public void save(Filters filter) {
        filterRepository.save(filter);
    }

    @Transactional
    public void update(int id, Filters filters) {
        Filters existingFilter = getFilterById(id);
        existingFilter.setNameOfFilter(filters.getNameOfFilter());
        existingFilter.setTypeOfFilter(filters.getTypeOfFilter());
        filterRepository.save(existingFilter);
    }



    @Transactional
    public Filters findOrCreate(String name) {
        return filterRepository.findByNameOfFilterIgnoreCase(name)
                .orElseGet(() -> {
                    Filters filter = new Filters();
                    filter.setNameOfFilter(name);

                    // Установим typeOfFilter как (максимальный + 1) или 1 если база пустая
                    Integer maxTypeOfFilter = filterRepository.findMaxTypeOfFilter();
                    filter.setTypeOfFilter(maxTypeOfFilter != null ? maxTypeOfFilter + 1 : 1);

                    return filterRepository.save(filter);
                });
    }


    public Filters getOrCreateFilterByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название фильтра не может быть пустым");
        }

        String normalizedName = name.trim().toLowerCase();

        return filterRepository.findByNameOfFilterIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    Filters newFilter = new Filters();
                    newFilter.setNameOfFilter(normalizedName);

                    // Автоматически генерируем уникальный typeOfFilter
                    Integer maxType = filterRepository.findMaxTypeOfFilter();
                    newFilter.setTypeOfFilter(maxType != null ? maxType + 1 : 0);

                    return filterRepository.save(newFilter);
                });
    }
}

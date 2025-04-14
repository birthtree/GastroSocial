package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Filters> filter = filterRepository.findById(id);
        return filter.orElseThrow(FilterNotFoundException::new);
    }

    @Transactional
    public void deleteById(int id) {
        if(!filterRepository.existsById(id)) {
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
        Filters filters1 = getFilterById(filters.getId());
        filters1.setNameOfFilter(filters.getNameOfFilter());
        filters1.setTypeOfFilter(filters.getTypeOfFilter());
        filterRepository.save(filters1);
    }

}

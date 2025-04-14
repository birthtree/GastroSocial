package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Menu;
import ru.akkuzin.vkr.backendVKR.repositories.MenuRepository;
import ru.akkuzin.vkr.backendVKR.util.Menu.MenuNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    @Autowired
    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> getAllMenu() {
        return menuRepository.findAll();
    }

    public Menu getMenuById(int id) {
        Optional<Menu> menu = menuRepository.findById(id);
        return menu.orElseThrow(MenuNotFoundException::new);
    }
    @Transactional
    public void deleteById(int id) {
        if(!menuRepository.existsById(id)) {
            throw new MenuNotFoundException();
        }
        menuRepository.deleteById(id);
    }
    @Transactional
    public void save(Menu menu) {
        menuRepository.save(menu);
    }
    @Transactional
    public void update(int id, Menu menu){
        Menu menuFromBD = getMenuById(menu.getId());
        menuFromBD.setName(menu.getName());
        menuRepository.save(menuFromBD);
    }

}

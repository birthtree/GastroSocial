package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.repositories.ReceptRepository;
import ru.akkuzin.vkr.backendVKR.util.PersonNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ReceptService {
    private final ReceptRepository receptRepository;

    @Autowired
    public ReceptService(ReceptRepository receptRepository) {
        this.receptRepository = receptRepository;
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
        receptRepository.save(recept);
    }
    @Transactional
    public void update(int id ,Recept recept) {
        //  Person personFromDb = peopleRepository.findById(id)
        //                    .orElseThrow(() -> new PersonNotFoundException());
        Recept receptFromDB = receptRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException());
        receptFromDB.setName(recept.getName());
         receptFromDB.setDiscription(recept.getDiscription());
        receptFromDB.setDuration(recept.getDuration());
        receptFromDB.setPrivate(recept.isPrivate());
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

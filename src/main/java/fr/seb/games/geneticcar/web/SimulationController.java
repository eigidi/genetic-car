package fr.seb.games.geneticcar.web;

import fr.seb.games.geneticcar.game.Game;
import fr.seb.games.geneticcar.simulation.Car;
import fr.seb.games.geneticcar.simulation.CarDefinition;
import fr.seb.games.geneticcar.simulation.Simulation;
import fr.seb.games.geneticcar.simulation.Team;
import fr.seb.games.geneticcar.web.dto.CarDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sebastien on 18/01/2015.
 */
@RestController
public class SimulationController {

    @RequestMapping(value="/simulation/evaluate/{team}", method = RequestMethod.POST)
    List<CarDto> evaluatePopulation(@RequestBody ArrayList<CarDto> carsDto, @PathVariable("team") Team team) {
        validate(carsDto, team);

        List<CarDefinition> definitions = carsDto.stream()
                .map(carDto -> carDto.toCarDefintion())
                .collect(Collectors.toList());

        Simulation simulation = Game.getSimulation(team);
        simulation.runSimulation(definitions);

        return simulation.allCars.stream()
                .map(car -> CarDto.create(team, car))
                .collect(Collectors.toList());
    }

    @RequestMapping(value="/simulation/champions", method = RequestMethod.GET)
    List<CarDto> getChampions() {
        return Game.players().entrySet().stream()
                .map(entry -> CarDto.create(entry.getKey(), entry.getValue().leader.car))
                .collect(Collectors.toList());
    }

    @RequestMapping(value="/simulation/champions/{team}", method = RequestMethod.GET)
    CarDto getChampion(@PathVariable("team") Team team) {
        Car car = Game.getSimulation(team).leader.car;
        return CarDto.create(team, car);
    }

    private void validate(ArrayList<CarDto> carsDto, Team team) {
        carsDto.forEach(carDto -> validate(carDto, team));
    }

    private void validate(CarDto carDto, Team team) {
        validate(carDto.wheel2, team);
        validate(carDto.wheel1, team);
        validate(carDto.chassi, team);
    }

    // TODO ajouter le controle sur la taille des listes
    private void validate(CarDto.Chassi chassi, Team team) {
        if (chassi.vecteurs.size() != 16) {
            throw new RuntimeException(team+" - le nombre de coordonnées est incorrect (!= de 16) : "+ chassi.vecteurs.size());
        }

        Float[] chassiTab = chassi.vecteurs.toArray(new Float[]{});
        validateRangeCoordChassi(Math.abs(chassiTab[0]), team);
        mustBePositif(chassiTab[0], team);
        mustBeZero(chassiTab[1], team);
        validateRangeCoordChassi(Math.abs(chassiTab[2]), team);
        mustBePositif(chassiTab[2], team);
        validateRangeCoordChassi(Math.abs(chassiTab[3]), team);
        mustBePositif(chassiTab[3], team);
        mustBeZero(chassiTab[4], team);
        validateRangeCoordChassi(Math.abs(chassiTab[5]), team);
        mustBePositif(chassiTab[5], team);
        validateRangeCoordChassi(Math.abs(chassiTab[6]), team);
        mustBeNegatif(chassiTab[6], team);
        validateRangeCoordChassi(Math.abs(chassiTab[7]), team);
        mustBePositif(chassiTab[7], team);
        validateRangeCoordChassi(Math.abs(chassiTab[8]), team);
        mustBeNegatif(chassiTab[8], team);
        mustBeZero(chassiTab[9], team);
        validateRangeCoordChassi(Math.abs(chassiTab[10]), team);
        mustBeNegatif(chassiTab[10], team);
        validateRangeCoordChassi(Math.abs(chassiTab[11]), team);
        mustBeNegatif(chassiTab[11], team);
        mustBeZero(chassiTab[12], team);
        validateRangeCoordChassi(Math.abs(chassiTab[13]), team);
        mustBeNegatif(chassiTab[13], team);
        validateRangeCoordChassi(Math.abs(chassiTab[14]), team);
        mustBePositif(chassiTab[14], team);
        validateRangeCoordChassi(Math.abs(chassiTab[15]), team);
        mustBeNegatif(chassiTab[15], team);

        if (chassi.densite < 30F || chassi.densite > 300F) {
            throw new RuntimeException(team+" - la densite du chassi n'est pas comprise entre 30 et 300 : "+chassi.densite);
        }
    }

    private void mustBeNegatif(Float coord, Team team) {
        if (coord > 0) {
            throw new RuntimeException(team+" - la coordonnee doit être positive : "+ coord);
        }
    }

    private void mustBePositif(Float coord, Team team) {
        if (coord < 0) {
            throw new RuntimeException(team+" - la coordonnee doit être positive : "+coord);
        }
    }

    private void mustBeZero(Float coord, Team team) {
        if (coord != 0) {
            throw new RuntimeException(team+" - la coordonnee doit valeur 0 : "+ coord);
        }
    }

    private void validateRangeCoordChassi(Float coord, Team team) {
        if (coord < 0.1F || coord > 1.1F) {
            throw new RuntimeException(team+" - la coordonnee n'est pas comprise entre 0.1 et 1.1 : "+coord);
        }
    }

    private void validate(CarDefinition.WheelDefinition wheel, Team team) {
        if (wheel.radius < 0.2 || wheel.radius > 0.5) {
            throw new RuntimeException(team+" - le rayon doit être compris entre 0.2 et 0.5 : "+wheel.radius);
        }
        if (wheel.density < 40 || wheel.density > 100) {
            throw new RuntimeException(team+" - la densite doit être comprise entre 40 et 100 : "+ wheel.density);
        }
        if (wheel.vertex < 0 || wheel.vertex > 7) {
            throw new RuntimeException(team+" - le sommet doit être compris entre 0 et 7 : "+ wheel.vertex);
        }

    }

}

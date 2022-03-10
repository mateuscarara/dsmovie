package com.devsuperior.dsmovie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.Movie;
import com.devsuperior.dsmovie.entities.Score;
import com.devsuperior.dsmovie.entities.User;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.repositories.UserRepository;

@Service
public class ScoreService {

	@Autowired
	private MovieRepository repository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ScoreRepository scoreRepository;
	
	@Transactional
	public MovieDTO saveScore(ScoreDTO dto) {
		
		//Recupera o email do usuário
		User user = userRepository.findByEmail(dto.getEmail());
		
		//Caso não tenha email, salva um novo no banco de dados
		if(user == null) {
			user = new User();
			user.setEmail(dto.getEmail());
			user = userRepository.saveAndFlush(user);
		}
		
		//Encontra o id do filme escolhido
		Movie movie = repository.findById(dto.getMovieId()).get();
		
		//Configura o filme, o usuário e o valor
		Score score = new Score();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(dto.getScore());
		
		//Salva o score
		score = scoreRepository.saveAndFlush(score);
		
		double sum = 0.0;
		
		//Percorre a lista de score calculando a soma
		for (Score s : movie.getScores()) {
			sum = sum + s.getValue();
		}
		
		//Calcula a média do score
		double avg = sum / movie.getScores().size();
		
		//Configura o score conforme a média
		movie.setScore(avg);
		
		//Configura a qtde de pessoas que avaliaram
		movie.setCount(movie.getScores().size());
		
		//Salva esse filme avaliado
		movie = repository.save(movie);
		
		return new MovieDTO(movie);
	}
}

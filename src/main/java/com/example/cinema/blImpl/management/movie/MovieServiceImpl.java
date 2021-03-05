package com.example.cinema.blImpl.management.movie;

import com.example.cinema.bl.management.MovieService;
import com.example.cinema.blImpl.management.schedule.ScheduleServiceForBl;
import com.example.cinema.data.management.MovieMapper;
import com.example.cinema.po.Movie;
import com.example.cinema.vo.MovieBatchOffForm;
import com.example.cinema.vo.MovieForm;
import com.example.cinema.vo.MovieVO;
import com.example.cinema.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fjj
 * @date 2019/3/12 6:43 PM
 */
@Service
public class MovieServiceImpl implements MovieService, MovieServiceForBl {
    private static final String SCHEDULE_ERROR_MESSAGE = "有电影后续仍有排片";

    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private ScheduleServiceForBl scheduleServiceForBl;

    @Override
    public ResponseVO addMovie(MovieForm addMovieForm) {
        try {
            movieMapper.insertOneMovie(addMovieForm);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO searchOneMovieByIdAndUserId(int id, int userId) {
        try {
            Movie movie = movieMapper.selectMovieByIdAndUserId(id, userId);
            if (movie != null) {
                return ResponseVO.buildSuccess(new MovieVO(movie));
            } else {
                return ResponseVO.buildSuccess(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }

    }

    @Override
    public ResponseVO searchAllMovie() {
        System.out.println("wq");
        try {
            List<Movie> movieBefore=movieMapper.selectAllMovie();
            List<Movie> xiajia=new ArrayList<>();
            List<Movie> result=new ArrayList<>();
            for(Movie m:movieBefore){
                if(m.getStatus()==1){
                    xiajia.add(m);
                }
                else{
                    result.add(m);
                }
            }
            for(Movie m:xiajia){
                result.add(m);
            }
            for(Movie m:result){
                System.out.println(m.getName());
            }
            return ResponseVO.buildSuccess(movieList2MovieVOList(result));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO searchOtherMoviesExcludeOff() {
        try {
            return ResponseVO.buildSuccess(movieList2MovieVOList(movieMapper.selectOtherMoviesExcludeOff()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO getMovieByKeyword(String keyword) {
        if (keyword == null || keyword.equals("")) {
            System.out.println("wq");
            try {
                List<Movie> movieBefore=movieMapper.selectAllMovie();
                List<Movie> xiajia=new ArrayList<>();
                List<Movie> result=new ArrayList<>();
                for(Movie m:movieBefore){
                    if(m.getStatus()==1){
                        xiajia.add(m);
                    }
                    else{
                        result.add(m);
                    }
                }
                for(Movie m:xiajia){
                    result.add(m);
                }
                for(Movie m:result){
                    System.out.println(m.getName());
                }
                return ResponseVO.buildSuccess(movieList2MovieVOList(result));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseVO.buildFailure("失败");
            }
//            return ResponseVO.buildSuccess(movieList2MovieVOList(movieMapper.selectAllMovie()));
        }
        return ResponseVO.buildSuccess(movieList2MovieVOList(movieMapper.selectMovieByKeyword(keyword)));
    }


    @Override
    public ResponseVO pullOfBatchOfMovie(MovieBatchOffForm movieBatchOffForm) {
        try {
            List<Integer> movieIdList = movieBatchOffForm.getMovieIdList();
            ResponseVO responseVO = preCheck(movieIdList);
            if (!responseVO.getSuccess()) {
                return responseVO;
            }
            movieMapper.updateMovieStatusBatch(movieIdList);
            return ResponseVO.buildSuccess();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public ResponseVO updateMovie(MovieForm updateMovieForm) {
        try {
            ResponseVO responseVO = preCheck(Arrays.asList(updateMovieForm.getId()));
            if (!responseVO.getSuccess()) {
                return responseVO;
            }
            movieMapper.updateMovie(updateMovieForm);
            return ResponseVO.buildSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("失败");
        }
    }

    @Override
    public Movie getMovieById(int id) {
        try {
            return movieMapper.selectMovieById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 下架和修改电影的前置检查
     *
     * @param movieIdList
     * @return
     */
    public ResponseVO preCheck(List<Integer> movieIdList) {
        for (Integer id : movieIdList) {
            Movie movie = movieMapper.selectMovieHasScheduleById(id);
            if (movie != null){
                return ResponseVO.buildFailure(SCHEDULE_ERROR_MESSAGE);
            }
        }
        return ResponseVO.buildSuccess();
    }


    private List<MovieVO> movieList2MovieVOList(List<Movie> movieList) {
        List<MovieVO> movieVOList = new ArrayList<>();
        for (Movie movie : movieList) {
            movieVOList.add(new MovieVO(movie));
        }
        return movieVOList;
    }

}

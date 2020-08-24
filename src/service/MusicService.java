package service;

import dao.MusicDao;
import entity.Music;

public class MusicService {
    public int deleteMusicById(int id){
        MusicDao musicDao = new MusicDao();
        int ret = musicDao.deleteMusicById(id);
        if(ret == 1){
            if(musicDao.findLoveMusicOnDel(id)){
                int  ret2 = musicDao.removeLoveMusicOnDelete(id);
                if(ret2 == 1){
                    return 1;
                }
            }else{
                return 1;
            }
        }
        return ret;
    }
    public Music findMusicById(int id){
        MusicDao musicDao = new MusicDao();
        Music music = musicDao.findMusicById(id);
        return music;
    }
}

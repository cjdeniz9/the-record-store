package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service
public class ProfileService
{
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    public Profile getByUserId(int userID) {
        return profileRepository.findByUserId(userID);
    }

    public Profile create(Profile profile)
    {
        return profileRepository.save(profile);
    }

    public Profile update(int userId, Profile profile) {
        Profile existing = profileRepository.findByUserId(userId);

        existing.setFirstName(profile.getFirstName());
        existing.setLastName(profile.getLastName());
        existing.setPhone(profile.getPhone());
        existing.setEmail(profile.getEmail());
        existing.setAddress(profile.getAddress());
        existing.setCity(profile.getCity());
        existing.setState(profile.getState());
        existing.setZip(profile.getZip());

        return profileRepository.save(existing);
    }
}

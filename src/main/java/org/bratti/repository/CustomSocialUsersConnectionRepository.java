package org.bratti.repository;

import org.apache.commons.lang3.RandomUtils;
import org.bratti.domain.Authority;
import org.bratti.domain.SocialUserConnection;
import org.bratti.domain.User;
import org.bratti.service.SocialService;
import org.bratti.service.UserService;
import org.bratti.service.UserServiceIntTest;
import org.springframework.social.connect.*;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomSocialUsersConnectionRepository implements UsersConnectionRepository {

    private SocialUserConnectionRepository socialUserConnectionRepository;

    private ConnectionFactoryLocator connectionFactoryLocator;

    private UserRepository userRepository;

    private AuthorityRepository authorityRepository;

    public CustomSocialUsersConnectionRepository(SocialUserConnectionRepository socialUserConnectionRepository, 
        ConnectionFactoryLocator connectionFactoryLocator,
        UserRepository userRepository,
        AuthorityRepository authorityRepository) {
        this.socialUserConnectionRepository = socialUserConnectionRepository;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<SocialUserConnection> socialUserConnections =
            socialUserConnectionRepository.findAllByProviderIdAndProviderUserId(key.getProviderId(), key.getProviderUserId());

        if (socialUserConnections.isEmpty()) {
            UserProfile profile = connection.fetchUserProfile();
            
            Set<Authority> authorities = new HashSet<>(1);
            authorities.add(authorityRepository.findOne("ROLE_USER"));

            User newUser = new User();
            newUser.setLogin(profile.getEmail());
            String senha = "";
            for (int i = 0; i < 60; i++)
                senha += RandomUtils.nextInt(0, 9);
            newUser.setPassword(senha);
            newUser.setFirstName(profile.getFirstName());
            newUser.setLastName(profile.getLastName());
            newUser.setEmail(profile.getEmail());
            newUser.setActivated(true);
            newUser.setAuthorities(authorities);
            newUser.setLangKey("pt");
            newUser.setImageUrl(connection.getProfileUrl());
            newUser.setActivated(true);
            userRepository.save(newUser);

            createConnectionRepository(profile.getEmail()).addConnection(connection);
            return Arrays.asList(profile.getEmail());
        }

        return socialUserConnections.stream()
            .map(SocialUserConnection::getUserId)
            .collect(Collectors.toList());
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        List<SocialUserConnection> socialUserConnections =
            socialUserConnectionRepository.findAllByProviderIdAndProviderUserIdIn(providerId, providerUserIds);
        return socialUserConnections.stream()
            .map(SocialUserConnection::getUserId)
            .collect(Collectors.toSet());
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        return new CustomSocialConnectionRepository(userId, socialUserConnectionRepository, connectionFactoryLocator);
    }
}

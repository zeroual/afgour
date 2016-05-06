package com.afgour.web.websocket;

import com.afgour.repository.ActiveSessionsRepository;
import com.afgour.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembersResource {

    private final Logger log = LoggerFactory.getLogger(MembersResource.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActiveSessionsRepository activeSessionsRepository;

    @RequestMapping(value = "/chat/members", method = RequestMethod.GET)
    public MembersDetails getUsersDetails() {
        MembersDetails membersDetails = new MembersDetails(userRepository.count(), activeSessionsRepository.count() + 1);
        log.info("members state > " + membersDetails.toString());
        return membersDetails;
    }

    private static class MembersDetails {
        private final int onLine;
        private final long total;

        private MembersDetails(long total, int onLine) {
            this.onLine = onLine;
            this.total = total;
        }

        public int getOnLine() {
            return onLine;
        }

        public long getTotal() {
            return total;
        }

        @Override
        public String toString() {
            return "MembersDetails{" +
                "onLine=" + onLine +
                ", total=" + total +
                '}';
        }
    }
}

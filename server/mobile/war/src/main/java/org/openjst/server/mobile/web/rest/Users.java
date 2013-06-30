package org.openjst.server.mobile.web.rest;

import org.openjst.server.commons.mq.IRestCrud;
import org.openjst.server.commons.mq.QueryListParams;
import org.openjst.server.mobile.mq.model.UserModel;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author Sergey Grachev
 */
@Path("/ui/users")
public interface Users extends IRestCrud<UserModel, Users.ListParamsParameters> {

    final class ListParamsParameters extends QueryListParams {
        @QueryParam("accountId")
        public Long accountId;
    }
}

package net.nighthawkempires.survival.user.registry;

import com.demigodsrpg.util.datasection.AbstractFileRegistry;
import net.nighthawkempires.survival.user.UserModel;

import java.util.Map;

public class FUserRegistry extends AbstractFileRegistry<UserModel> implements UserRegistry {
    private static final boolean SAVE_PRETTY = true;

    public FUserRegistry(String path) {
        super(path, NAME, SAVE_PRETTY, 5);
    }

    @Override
    public Map<String, UserModel> getRegisteredData() {
        return REGISTERED_DATA.asMap();
    }
}

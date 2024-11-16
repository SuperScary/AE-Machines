package ae2m.init;

import ae2m.blockentity.machine.FurnaceBlockEntity;
import ae2m.core.AE2M;
import ae2m.menu.implementations.FurnaceMenu;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AE2MMenuTypes {

    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, AE2M.MOD_ID);

    public static final MenuType<FurnaceMenu> FURNACE_MENU = create("furnace", FurnaceMenu::new, FurnaceBlockEntity.class);

    private static <M extends AEBaseMenu, H> MenuType<M> create (String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        var menu = MenuTypeBuilder.create(factory, host).build(AE2M.getResource(id));
        DR.register(id, () -> menu);
        return menu;
    }

}

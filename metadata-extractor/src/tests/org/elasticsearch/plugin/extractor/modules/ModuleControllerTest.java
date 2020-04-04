package org.elasticsearch.plugin.extractor.modules;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ModuleControllerTest {

    @Test
    void getInstance() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ModuleController module_controller = ModuleController.getInstance();
        assertEquals(module_controller,ModuleController.getInstance());
        assertNotNull(module_controller);
    }



    @Test
    void containsModuleForExtention() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String valid = "pdf";
        String not_valid_1 = "not_valid_extention";
        String not_valid_2 = "";
        String not_valid_3 = ".pdf";
        assertTrue(ModuleController.getInstance().containsModuleForExtention(valid));
        assertFalse(ModuleController.getInstance().containsModuleForExtention(not_valid_1));
        assertFalse(ModuleController.getInstance().containsModuleForExtention(not_valid_2));
        assertFalse(ModuleController.getInstance().containsModuleForExtention(not_valid_3));
    }
}
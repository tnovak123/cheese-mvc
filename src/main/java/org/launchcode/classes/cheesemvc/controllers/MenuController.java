package org.launchcode.classes.cheesemvc.controllers;

import org.launchcode.classes.cheesemvc.models.Cheese;
import org.launchcode.classes.cheesemvc.models.Menu;
import org.launchcode.classes.cheesemvc.models.data.CheeseDao;
import org.launchcode.classes.cheesemvc.models.data.MenuDao;
import org.launchcode.classes.cheesemvc.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;

    private static String area = "menu";

    @RequestMapping(value = "")
    public String index(Model model){

        model.addAttribute("title", "Menus");
        model.addAttribute("area", area);
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {

        model.addAttribute("title", "Add Menu");
        model.addAttribute("area", area);
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors){

        if (errors.hasErrors()){
            model.addAttribute("title", "Add Menu");
            model.addAttribute("area", area);
            return "menu/add";
        }

        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId){

        Menu menu = menuDao.findById(menuId).get();

        model.addAttribute("title", menu.getName());
        model.addAttribute("cheeses", menu.getCheeses());
        model.addAttribute("menuId", menu.getId());

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId){

        Menu menu = menuDao.findById(menuId).get();

        AddMenuItemForm form = new AddMenuItemForm(cheeseDao.findAll(), menu);

        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("form", form);
        model.addAttribute("area", area);
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors){

        if (errors.hasErrors()){
            model.addAttribute("title", "Add item to menu: " + menuDao.findById(form.getMenuId()).get().getName());
            model.addAttribute("form", form);
            model.addAttribute("area", area);
            return "menu/add-item";
        }

        Cheese theCheese = cheeseDao.findById(form.getCheeseId()).get();
        Menu theMenu = menuDao.findById(form.getMenuId()).get();
        theMenu.addItem(theCheese);
        menuDao.save(theMenu);

        return "redirect:/menu/view/" + theMenu.getId();
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemoveMenuForm(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("area", area);
        model.addAttribute("title", "Remove Menus");
        return "menu/remove";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemoveMenuForm(@RequestParam int[] menuIds) {

        for (int menuId : menuIds) {
            cheeseDao.deleteById(menuId);
        }

        return "redirect:";
    }

}

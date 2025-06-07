package kr.ac.hansung.cse.hellospringdatajpa.controller;

import kr.ac.hansung.cse.hellospringdatajpa.entity.Product;
import kr.ac.hansung.cse.hellospringdatajpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 상품 목록 조회
    @GetMapping("")
    public String listProducts(Model model) {
        List<Product> products = productService.listAll();
        model.addAttribute("products", products);
        return "products/list";
    }

    // 상품 등록 폼
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/new";
    }

    // 상품 등록 처리
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute Product product,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {

        // 추가 유효성 검사
        if (product.getPrice() != null && product.getPrice() < 0) {
            result.rejectValue("price", "error.product", "가격은 0 이상이어야 합니다.");
        }

        if (result.hasErrors()) {
            return "products/new"; // 수정: form -> new
        }

        try {
            productService.save(product);
            redirectAttributes.addFlashAttribute("successMsg", "상품이 성공적으로 등록되었습니다.");
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "상품 등록 중 오류가 발생했습니다.");
            return "redirect:/products";
        }
    }

    // 상품 상세 조회 - URL 패턴 변경
    @GetMapping("/{id}/view")
    public String view(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/view";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 상품입니다.");
            return "redirect:/products";
        }
    }

    // 상품 수정 폼
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/form";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 상품입니다.");
            return "redirect:/products"; // 수정: products/ -> redirect:/products
        }
    }

    // 상품 수정 처리
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Product product,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {

        // 추가 유효성 검사
        if (product.getPrice() != null && product.getPrice() < 0) {
            result.rejectValue("price", "error.product", "가격은 0 이상이어야 합니다.");
        }

        if (result.hasErrors()) {
            product.setId(id); // 폼에서 ID 유지
            return "products/form";
        }

        try {
            // 기존 상품 존재 확인
            productService.get(id);

            product.setId(id);
            productService.save(product);
            redirectAttributes.addFlashAttribute("successMsg", "상품이 성공적으로 수정되었습니다.");
            // 수정 후 해당 상품의 상세 페이지로 리다이렉트
            return "redirect:/products/" + id + "/view";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 상품입니다.");
            return "redirect:/products"; // 수정
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "상품 수정 중 오류가 발생했습니다.");
            return "redirect:/products"; // 수정
        }
    }

    // 상품 삭제 처리
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // 기존 상품 존재 확인
            productService.get(id);

            productService.delete(id);
            redirectAttributes.addFlashAttribute("successMsg", "상품이 성공적으로 삭제되었습니다.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 상품입니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "상품 삭제 중 오류가 발생했습니다.");
        }

        return "redirect:/products";
    }
}
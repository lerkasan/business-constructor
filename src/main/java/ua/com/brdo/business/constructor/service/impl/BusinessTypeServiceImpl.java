package ua.com.brdo.business.constructor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import ua.com.brdo.business.constructor.constraint.UniqueValidatable;
import ua.com.brdo.business.constructor.model.BusinessType;
import ua.com.brdo.business.constructor.repository.BusinessTypeRepository;
import ua.com.brdo.business.constructor.service.BusinessTypeService;
import ua.com.brdo.business.constructor.service.NotFoundException;

import static java.util.Objects.isNull;

@Service
public class BusinessTypeServiceImpl implements BusinessTypeService, UniqueValidatable {

    private BusinessTypeRepository businessTypeRepo;

    @Autowired
    public BusinessTypeServiceImpl(BusinessTypeRepository businessTypeRepo) {
        this.businessTypeRepo = businessTypeRepo;
    }

    @Transactional
    @Override
    public BusinessType create(final BusinessType businessType) {
        Objects.requireNonNull(businessType);
        return businessTypeRepo.saveAndFlush(businessType);
    }

    @Transactional
    @Override
    public BusinessType update(@Valid final BusinessType businessType) {
        Objects.requireNonNull(businessType);
        return businessTypeRepo.saveAndFlush(businessType);
    }

    @Transactional
    @Override
    public void delete(final long id) {
        businessTypeRepo.delete(id);
    }

    @Transactional
    @Override
    public void delete(final BusinessType businessType) {
        businessTypeRepo.delete(businessType);
    }

    @Override
    public BusinessType findById(final long id) {
        BusinessType businessType = businessTypeRepo.findOne(id);
        if (businessType == null) {
            throw new NotFoundException("Specified business type was not found.");
        }
        return businessType;
    }

    @Override
    public BusinessType findByTitle(final String title) {
        return businessTypeRepo.findByTitle(title)
            .orElseThrow(
                () -> new NotFoundException("Business type with given title was not found."));
    }

    @Override
    public BusinessType findByCodeKved(final String codeKved) {
        return businessTypeRepo.findByCodeKved(codeKved)
            .orElseThrow(
                () -> new NotFoundException("Business type with given code kved was not found."));
    }

    @Override
    public List<BusinessType> findAll() {
        return businessTypeRepo.findAll();
    }

    public boolean isAvailable(String fieldName, String fieldValue, Long id) {
        if (isNull(fieldValue)) {
            return false;
        }
        switch (fieldName) {
            case "title":
                return businessTypeRepo.titleAvailable(fieldValue, id);
            case "codeKved":
                return businessTypeRepo.codeKvedAvailable(fieldValue, id);
            default:
                throw new IllegalArgumentException(
                    "Unexpected field was passed to isAvailable method.");
        }
    }
}

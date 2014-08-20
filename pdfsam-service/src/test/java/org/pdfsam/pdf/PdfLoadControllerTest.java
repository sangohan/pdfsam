/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 19/ago/2014
 * Copyright 2013-2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.pdf;

import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pdfsam.module.Module;
import org.pdfsam.module.ModuleDescriptor;
import org.pdfsam.module.RequiredPdfData;
import org.pdfsam.test.ClearEventStudioRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Andrea Vacondio
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PdfLoadControllerTest {

    @Rule
    public ClearEventStudioRule clearStudio = new ClearEventStudioRule();
    @Inject
    private PdfLoadService loadService;
    @Inject
    private PdfLoadController victim;

    @Configuration
    static class Config {
        @Bean
        public PdfLoadService service() {
            return mock(PdfLoadService.class);
        }

        @Bean
        public PdfLoadController controller() {
            return new PdfLoadController(Arrays.asList(new Module[] { new Module() {

                public Pane modulePanel() {
                    return null;
                }

                public String id() {
                    return "moduleId";
                }

                public Node graphic() {
                    return null;
                }

                public ModuleDescriptor descriptor() {
                    return null;
                }
            } }));
        }
    }

    @Test
    public void request() {
        PdfLoadRequestEvent<PdfDocumentDescriptor> event = new PdfLoadRequestEvent<>("moduleId");
        PdfDocumentDescriptor first = mock(PdfDocumentDescriptor.class);
        PdfDocumentDescriptor second = mock(PdfDocumentDescriptor.class);
        event.add(first);
        event.add(second);
        victim.request(event);
        verify(first).moveStatusTo(PdfDescriptorLoadingStatus.REQUESTED);
        verify(second).moveStatusTo(PdfDescriptorLoadingStatus.REQUESTED);
        verify(loadService, timeout(1000).times(1)).load(anyCollectionOf(PdfDocumentDescriptor.class),
                eq(RequiredPdfData.DEFAULT));
    }
}
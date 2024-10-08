/*
 * SonarQube
 * Copyright (C) 2009-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.measure;

import java.util.Arrays;
import java.util.function.Consumer;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ProjectData;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.metric.MetricDto;

import static org.sonar.db.measure.MeasureTesting.newLiveMeasure;
import static org.sonar.db.measure.MeasureTesting.newProjectMeasureDto;
import static org.sonar.db.metric.MetricTesting.newMetricDto;

public class MeasureDbTester {
  private final DbClient dbClient;
  private final DbSession dbSession;

  public MeasureDbTester(DbTester db) {
    this.dbClient = db.getDbClient();
    this.dbSession = db.getSession();
  }

  @SafeVarargs
  public final ProjectMeasureDto insertProjectMeasure(ComponentDto component, SnapshotDto analysis, MetricDto metricDto, Consumer<ProjectMeasureDto>... consumers) {
    ProjectMeasureDto projectMeasureDto = newProjectMeasureDto(metricDto, component, analysis);
    Arrays.stream(consumers).forEach(c -> c.accept(projectMeasureDto));
    dbClient.projectMeasureDao().insert(dbSession, projectMeasureDto);
    dbSession.commit();
    return projectMeasureDto;
  }

  @SafeVarargs
  public final ProjectMeasureDto insertProjectMeasure(BranchDto branchDto, SnapshotDto analysis, MetricDto metricDto, Consumer<ProjectMeasureDto>... consumers) {
    ProjectMeasureDto projectMeasureDto = MeasureTesting.newProjectMeasureDto(metricDto, branchDto.getUuid(), analysis);
    Arrays.stream(consumers).forEach(c -> c.accept(projectMeasureDto));
    dbClient.projectMeasureDao().insert(dbSession, projectMeasureDto);
    dbSession.commit();
    return projectMeasureDto;
  }

  @SafeVarargs
  public final LiveMeasureDto insertLiveMeasure(ComponentDto component, MetricDto metric, Consumer<LiveMeasureDto>... consumers) {
    LiveMeasureDto dto = newLiveMeasure(component, metric);
    Arrays.stream(consumers).forEach(c -> c.accept(dto));
    dbClient.liveMeasureDao().insert(dbSession, dto);
    dbSession.commit();
    return dto;
  }

  @SafeVarargs
  public final LiveMeasureDto insertLiveMeasure(BranchDto branchDto, MetricDto metric, Consumer<LiveMeasureDto>... consumers) {
    LiveMeasureDto dto = newLiveMeasure(branchDto, metric);
    Arrays.stream(consumers).forEach(c -> c.accept(dto));
    dbClient.liveMeasureDao().insert(dbSession, dto);
    dbSession.commit();
    return dto;
  }

  @SafeVarargs
  public final LiveMeasureDto insertLiveMeasure(ProjectData projectData, MetricDto metric, Consumer<LiveMeasureDto>... consumers) {
    return insertLiveMeasure(projectData.getMainBranchComponent(), metric, consumers);
  }


  @SafeVarargs
  public final MetricDto insertMetric(Consumer<MetricDto>... consumers) {
    MetricDto metricDto = newMetricDto();
    Arrays.stream(consumers).forEach(c -> c.accept(metricDto));
    dbClient.metricDao().insert(dbSession, metricDto);
    dbSession.commit();
    return metricDto;
  }

}
